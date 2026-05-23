package com.example

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.math.absoluteValue

data class GeneratedContent(
    val caption: String,
    val hashtags: String,
    val description: String,
    val rawText: String = ""
)

data class LinkAnalysis(
    val link: String,
    val viralScore: Int,
    val estimatedViews: String,
    val engagementRate: String,
    val bestUploadTime: String
)

sealed class GenerationState {
    object Idle : GenerationState()
    object Loading : GenerationState()
    data class Success(val result: GeneratedContent) : GenerationState()
    data class Error(val message: String) : GenerationState()
}

class MainViewModel : ViewModel() {
    private val _state = MutableStateFlow<GenerationState>(GenerationState.Idle)
    val state: StateFlow<GenerationState> = _state.asStateFlow()

    private val _linkAnalysis = MutableStateFlow<LinkAnalysis?>(null)
    val linkAnalysis: StateFlow<LinkAnalysis?> = _linkAnalysis.asStateFlow()

    fun generateViralContent(
        topic: String,
        platform: String,
        contentType: String,
        tone: String,
        language: String
    ) {
        if (topic.isBlank()) {
            _state.value = GenerationState.Error("Please enter a topic.")
            return
        }
        
        _state.value = GenerationState.Loading

        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            try {
                val prompt = """
                    Act as an expert social media manager and viral content creator.
                    Generate a highly engaging, viral $contentType for $platform.
                    
                    Topic: $topic
                    Tone: $tone
                    Language: $language
                    
                    IMPORTANT: You MUST respond purely in valid JSON format. Do not use markdown blocks like ```json.
                    Format your response EXACTLY like this:
                    {
                       "caption": "The main content text here (if no caption needed, provide a catchy title)",
                       "hashtags": "#tag1 #tag2 #trending ...",
                       "description": "Detailed description or SEO text here"
                    }
                """.trimIndent()

                val request = GenerateContentRequest(
                    contents = listOf(Content(parts = listOf(Part(text = prompt))))
                )

                val apiKey = BuildConfig.GEMINI_API_KEY
                
                if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
                     _state.value = GenerationState.Error("Invalid API Key. Please set GEMINI_API_KEY in the Secrets panel.")
                     return@launch
                }

                val response = RetrofitClient.service.generateContent(apiKey, request)
                
                var text = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                    ?: "Failed to generate content."
                
                // Robust JSON string extraction
                var parsedCaption = ""
                var parsedHashtags = ""
                var parsedDescription = ""
                
                var jsonStr = text
                val startIndex = text.indexOf('{')
                val endIndex = text.lastIndexOf('}')
                if (startIndex != -1 && endIndex != -1 && endIndex > startIndex) {
                    jsonStr = text.substring(startIndex, endIndex + 1)
                }
                
                try {
                    val jsonObj = JSONObject(jsonStr)
                    parsedCaption = jsonObj.optString("caption", "")
                    parsedHashtags = jsonObj.optString("hashtags", "")
                    parsedDescription = jsonObj.optString("description", "")
                } catch (e: Exception) {
                    // Fallback if parsing fails
                    parsedCaption = text
                }
                
                if (parsedCaption.isBlank() && parsedHashtags.isBlank() && parsedDescription.isBlank()) {
                    parsedCaption = text
                }

                _state.value = GenerationState.Success(
                    GeneratedContent(
                        caption = parsedCaption.ifEmpty { text },
                        hashtags = parsedHashtags,
                        description = parsedDescription,
                        rawText = text
                    )
                )
            } catch (e: retrofit2.HttpException) {
                if (e.code() == 429) {
                     val fallbackContent = GeneratedContent(
                        caption = "Here is an amazing viral fallback caption because the AI servers are too busy! \n\nSmash that like button if you agree! 🔥🔥",
                        hashtags = "#viral #fallback #trending #${platform.lowercase().replace(" ", "")}",
                        description = "This is a fallback SEO description generated because the AI rate limit (HTTP 429) was reached. Try again later for real AI generation."
                     )
                     _state.value = GenerationState.Success(fallbackContent)
                } else {
                     _state.value = GenerationState.Error("API Error: ${e.code()}")
                }
            } catch (e: Exception) {
                if (e.message?.contains("429") == true) {
                     val fallbackContent = GeneratedContent(
                        caption = "Here is an amazing viral fallback caption because the AI servers are too busy! \n\nSmash that like button if you agree! 🔥🔥",
                        hashtags = "#viral #fallback #trending #${platform.lowercase().replace(" ", "")}",
                        description = "This is a fallback SEO description generated because the AI rate limit (HTTP 429) was reached. Try again later for real AI generation."
                     )
                     _state.value = GenerationState.Success(fallbackContent)
                } else {
                     _state.value = GenerationState.Error("Error: ${e.message}")
                }
            }
        }
    }
    
    private val _instaPostData = MutableStateFlow<String?>(null)
    val instaPostData: StateFlow<String?> = _instaPostData.asStateFlow()

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    fun analyzeLink(link: String) {
        if (link.isBlank()) return
        
        viewModelScope.launch(kotlinx.coroutines.Dispatchers.IO) {
            _isAnalyzing.value = true
            _linkAnalysis.value = null
            _instaPostData.value = null
            
            // Generate deterministic analysis based on link string
            val hash = link.hashCode() and 0x7FFFFFFF
            val score = 75 + (hash % 21) // 75 to 95
            
            val viewsMultiplier = (hash % 10) + 1
            val viewsMagnitude = arrayOf("K", "M")[hash % 2]
            val viewsStr = "${(15..150).random(kotlin.random.Random(hash))} $viewsMultiplier$viewsMagnitude+"
            
            val engRate = 3.5 + (hash % 100) / 10.0
            val times = arrayOf("9:00 AM", "1:30 PM", "6:00 PM", "7:30 PM", "8:45 PM")
            
            _linkAnalysis.value = LinkAnalysis(
                link = link,
                viralScore = score,
                estimatedViews = viewsStr,
                engagementRate = String.format("%.1f%%", engRate),
                bestUploadTime = times[hash % times.size]
            )
            
            // Try fetch Instagram
            var username = ""
            if (link.contains("instagram.com/")) {
                val parts = link.split("instagram.com/")
                if (parts.size > 1) {
                    username = parts[1].split("/")[0].split("?")[0]
                }
            } else if (!link.startsWith("http") && !link.contains(" ")) {
                username = link
            }
            
            if (username.isNotEmpty()) {
                try {
                    val req = InstaRequest(username = username)
                    val response = RetrofitClient.instagramService.getPosts(body = req).string()
                    _instaPostData.value = response.take(150) + "..." // Limit output for preview
                } catch(e: Throwable) {
                    _instaPostData.value = "Error fetching posts for @$username: ${e.message}"
                }
            }
            _isAnalyzing.value = false
        }
    }

    fun reset() {
        _state.value = GenerationState.Idle
    }
}

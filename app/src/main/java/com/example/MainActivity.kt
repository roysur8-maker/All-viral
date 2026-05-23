package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                ViralCaptionApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViralCaptionApp(viewModel: MainViewModel = viewModel()) {
    val state by viewModel.state.collectAsState()
    
    var topic by remember { mutableStateOf("") }
    var selectedPlatform by remember { mutableStateOf("Instagram") }
    var selectedType by remember { mutableStateOf("Caption") }
    var selectedTone by remember { mutableStateOf("Viral") }
    var selectedLanguage by remember { mutableStateOf("English") }
    
    val platforms = listOf("Instagram", "YouTube", "TikTok", "X (Twitter)", "Facebook", "Threads")
    val types = listOf("Caption", "Hashtags", "YouTube Tags", "Reels Hook", "Shorts Caption")
    val tones = listOf("Viral", "Funny", "Attitude", "Professional", "Cool")
    val languages = listOf("English", "Hindi", "Bengali", "Bhojpuri", "Mixed Language")

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "AI", tint = PinkAccent)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "ViralCaption AI", 
                            color = TextWhite, 
                            fontWeight = FontWeight.Bold
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = TextWhite
                )
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Background Animation (Floating gradient blobs)
            AnimatedBackground()
            
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxHeight()
                        .widthIn(max = 800.dp)
                        .padding(horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    item { Spacer(modifier = Modifier.height(32.dp)) }
                
                item {
                    val titleGradient = Brush.horizontalGradient(
                        colors = listOf(TextWhite, TextWhite, TextWhite.copy(alpha=0.4f))
                    )
                    Text(
                        text = "CREATE VIRAL CAPTIONS & HASHTAGS WITH AI",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-1).sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 40.sp,
                        modifier = Modifier.padding(bottom = 16.dp),
                        style = androidx.compose.ui.text.TextStyle(brush = titleGradient)
                    )
                    
                    Text(
                        text = "Generate trending captions, viral hashtags, YouTube tags, reels text, and social media content instantly.",
                        fontSize = 16.sp,
                        color = TextGray,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )
                    
                    AnimatedCounter()
                    TypingTextAnimation()
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
                
                item {
                    GlassCard {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = topic,
                                onValueChange = { topic = it },
                                label = { Text("Content Topic", color = TextGray) },
                                placeholder = { Text("Enter video idea, vlog, clip...", color = Color.Gray) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = CyanAccent,
                                    unfocusedBorderColor = CardBorder,
                                    focusedTextColor = TextWhite,
                                    unfocusedTextColor = TextWhite,
                                    focusedContainerColor = CardBackground,
                                    unfocusedContainerColor = CardBackground
                                ),
                                shape = RoundedCornerShape(12.dp)
                            )
                            
                            CustomDropdown("Platform", selectedPlatform, platforms) { selectedPlatform = it }
                            CustomDropdown("Content Type", selectedType, types) { selectedType = it }
                            CustomDropdown("Tone", selectedTone, tones) { selectedTone = it }
                            CustomDropdown("Language", selectedLanguage, languages) { selectedLanguage = it }
                            
                            Spacer(Modifier.height(8.dp))
                            
                            Button(
                                onClick = { 
                                    viewModel.generateViralContent(topic, selectedPlatform, selectedType, selectedTone, selectedLanguage) 
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(56.dp)
                                    .shadow(
                                        elevation = 16.dp, 
                                        shape = RoundedCornerShape(24.dp)
                                    )
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(
                                        brush = Brush.horizontalGradient(listOf(PurpleAccent, PinkAccent, CyanAccent))
                                    ),
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                contentPadding = PaddingValues()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Bolt, contentDescription = null, tint = TextWhite)
                                    Spacer(Modifier.width(8.dp))
                                    Text("GENERATE CONTENT", color = TextWhite, fontWeight = FontWeight.Black, fontSize = 14.sp, letterSpacing = 2.sp)
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
                
                item {
                    AnimatedContent(
                        targetState = state,
                        transitionSpec = {
                            fadeIn() + slideInVertically { it / 2 } togetherWith fadeOut() + slideOutVertically { -it / 2 }
                        }, label = "result"
                    ) { currentState ->
                        when(currentState) {
                            is GenerationState.Idle -> { }
                            is GenerationState.Loading -> {
                                LoadingAnimation()
                            }
                            is GenerationState.Success -> {
                                ResultCard(currentState.result, viewModel)
                            }
                            is GenerationState.Error -> {
                                Text(currentState.message, color = PinkAccent, modifier = Modifier.padding(16.dp))
                            }
                        }
                    }
                    
                    if (state !is GenerationState.Idle) {
                        Spacer(modifier = Modifier.height(64.dp))
                    }
                }
                
                // Extra sections
                item {
                    LinkAnalyzerSection(viewModel)
                    Spacer(modifier = Modifier.height(64.dp))
                }

                item {
                    TrendingSection()
                    Spacer(modifier = Modifier.height(64.dp))
                }
            }
            }
        }
    }
}

@Composable
fun AnimatedBackground() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .offset(x = 100.dp, y = (-50).dp)
                .size(300.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(DarkPurpleBlur, Color.Transparent)
                    )
                )
                .align(Alignment.TopEnd)
        )
        Box(
            modifier = Modifier
                .offset(x = (-50).dp, y = 50.dp)
                .size(300.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(CyanBlur, Color.Transparent)
                    )
                )
                .align(Alignment.BottomStart)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDropdown(label: String, selected: String, items: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text(label, color = TextGray) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = CyanAccent,
                unfocusedBorderColor = CardBorder,
                focusedTextColor = TextWhite,
                unfocusedTextColor = TextWhite,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent
            ),
            modifier = Modifier.menuAnchor().fillMaxWidth(),
            shape = RoundedCornerShape(12.dp)
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.background(CardBackground)
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(item, color = TextWhite) },
                    onClick = {
                        onSelect(item)
                        expanded = false
                    },
                    modifier = Modifier.background(CardBackground)
                )
            }
        }
    }
}

@Composable
fun GlassCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val infiniteTransition = rememberInfiniteTransition()
    val borderColor by infiniteTransition.animateColor(
        initialValue = PurpleAccent.copy(alpha = 0.3f),
        targetValue = CyanAccent.copy(alpha = 0.3f),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ), label = "border"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(32.dp))
            .background(CardBackground)
            .border(1.dp, borderColor, RoundedCornerShape(32.dp))
            .padding(1.dp) // inner padding for blur
    ) {
        content()
    }
}

@Composable
fun AnimatedCounter() {
    var text by remember { mutableStateOf("12,493 captions generated today") }
    
    LaunchedEffect(Unit) {
        var count = 12493
        while(true) {
            delay(3000)
            count += (1..5).random()
            text = "$count captions generated today"
        }
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(CyanAccent.copy(alpha = 0.1f))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text, color = CyanAccent, fontWeight = FontWeight.SemiBold)
    }
}

@Composable
fun TypingTextAnimation() {
    val phrases = listOf("Instagram Captions", "YouTube Tags", "Viral Reels Ideas", "Trending Hashtags", "AI Social Media Growth")
    var currentPhraseIndex by remember { mutableStateOf(0) }
    var displayedText by remember { mutableStateOf("") }
    
    LaunchedEffect(Unit) {
        while(true) {
            val targetPhrase = phrases[currentPhraseIndex]
            for (i in 0..targetPhrase.length) {
                displayedText = targetPhrase.substring(0, i)
                delay(50)
            }
            delay(1500)
            for (i in targetPhrase.length downTo 0) {
                displayedText = targetPhrase.substring(0, i)
                delay(30)
            }
            currentPhraseIndex = (currentPhraseIndex + 1) % phrases.size
        }
    }
    
    Text(
        text = "⚡ $displayedText|",
        color = PinkAccent,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(top = 16.dp)
    )
}

@Composable
fun LoadingAnimation() {
    val transition = rememberInfiniteTransition()
    val alpha by transition.animateFloat(
        initialValue = 0.3f, 
        targetValue = 1f, 
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "loading"
    )
    
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = CyanAccent.copy(alpha=alpha), modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(16.dp))
        Text("AI is generating your viral content...", color = TextGray, modifier = Modifier.alpha(alpha))
    }
}

@Composable
fun ResultCard(result: GeneratedContent, viewModel: MainViewModel) {
    val clipboardManager = LocalClipboardManager.current
    var copiedItem by remember { mutableStateOf<String?>(null) }
    
    Card(
        modifier = Modifier.fillMaxWidth().border(1.dp, GreenAccent.copy(alpha=0.5f), RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, null, tint = GreenAccent)
                Spacer(Modifier.width(8.dp))
                Text("Content Ready! (94% Viral Potential)", color = GreenAccent, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(16.dp))
            
            // Caption Section
            if (result.caption.isNotBlank()) {
                Text("Caption", color = PinkAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom=4.dp))
                androidx.compose.foundation.text.selection.SelectionContainer {
                    Text(result.caption, color = TextWhite, style = MaterialTheme.typography.bodyLarge)
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { 
                        clipboardManager.setText(AnnotatedString(result.caption))
                        copiedItem = "caption"
                    },
                    modifier = Modifier.height(32.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent.copy(alpha = 0.2f), contentColor = CyanAccent),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Icon(if(copiedItem == "caption") Icons.Default.Check else Icons.Default.ContentCopy, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if(copiedItem == "caption") "Copied Caption!" else "Copy Caption", fontSize = 12.sp)
                }
                Spacer(Modifier.height(16.dp))
            }
            
            // Hashtags Section
            if (result.hashtags.isNotBlank()) {
                Text("Hashtags", color = PinkAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom=4.dp))
                androidx.compose.foundation.text.selection.SelectionContainer {
                    Text(result.hashtags, color = CyanAccent, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { 
                        clipboardManager.setText(AnnotatedString(result.hashtags))
                        copiedItem = "hashtags"
                    },
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent.copy(alpha = 0.2f), contentColor = CyanAccent),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Icon(if(copiedItem == "hashtags") Icons.Default.Check else Icons.Default.ContentCopy, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if(copiedItem == "hashtags") "Copied Hashtags!" else "Copy Hashtags", fontSize = 12.sp)
                }
                Spacer(Modifier.height(16.dp))
            }

            // Description Section
            if (result.description.isNotBlank()) {
                Text("Description / SEO", color = PinkAccent, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom=4.dp))
                androidx.compose.foundation.text.selection.SelectionContainer {
                    Text(result.description, color = TextGray, style = MaterialTheme.typography.bodyMedium)
                }
                Spacer(Modifier.height(8.dp))
                Button(
                    onClick = { 
                        clipboardManager.setText(AnnotatedString(result.description))
                        copiedItem = "description"
                    },
                    modifier = Modifier.height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CyanAccent.copy(alpha = 0.2f), contentColor = CyanAccent),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                ) {
                    Icon(if(copiedItem == "description") Icons.Default.Check else Icons.Default.ContentCopy, null, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(if(copiedItem == "description") "Copied Description!" else "Copy Description", fontSize = 12.sp)
                }
                Spacer(Modifier.height(16.dp))
            }
            
            Spacer(Modifier.height(16.dp))
            
            Divider(color = Color.White.copy(alpha = 0.1f))
            Spacer(Modifier.height(16.dp))
            
            Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(
                    onClick = { viewModel.reset() },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = TextWhite)
                ) {
                    Icon(Icons.Default.Refresh, null)
                    Spacer(Modifier.width(8.dp))
                    Text("Generate New")
                }
            }
        }
    }
}

@Composable
fun TrendingSection() {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("Trending Right Now #️⃣", fontSize = 20.sp, color = TextWhite, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(16.dp))
        
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Chip("#viral")
            Chip("#fyp")
            Chip("#trending")
        }
        Spacer(Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Chip("#instagramviral")
            Chip("#ai")
            Chip("#growth")
        }
    }
}

@Composable
fun Chip(text: String) {
    Box(modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .background(Color.White.copy(alpha = 0.1f))
        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
        .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(text, color = TextGray)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LinkAnalyzerSection(viewModel: MainViewModel) {
    val analysis by viewModel.linkAnalysis.collectAsState()
    val isAnalyzing by viewModel.isAnalyzing.collectAsState()
    var link by remember { mutableStateOf("") }
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Text("AI Viral Prediction 🔮", fontSize = 20.sp, color = TextWhite, fontWeight = FontWeight.Bold)
        Text("Paste a link to analyze its viral potential", fontSize = 14.sp, color = TextGray)
        Spacer(Modifier.height(16.dp))
        
        GlassCard {
            Column(modifier = Modifier.padding(24.dp)) {
                OutlinedTextField(
                    value = link,
                    onValueChange = { link = it },
                    label = { Text("Video or Post Link", color = TextGray) },
                    placeholder = { Text("https://instagram.com/p/...", color = Color.Gray) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = CyanAccent,
                        unfocusedBorderColor = CardBorder,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground
                    ),
                    shape = RoundedCornerShape(12.dp)
                )
                
                Spacer(Modifier.height(16.dp))
                
                Button(
                    onClick = { viewModel.analyzeLink(link) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.horizontalGradient(listOf(PurpleAccent, PinkAccent))
                        ),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues()
                ) {
                    Icon(Icons.Default.Analytics, contentDescription = null, tint = TextWhite)
                    Spacer(Modifier.width(8.dp))
                    Text("Analyze Link", color = TextWhite, fontWeight = FontWeight.Bold)
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        if (isAnalyzing) {
            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                LoadingAnimation()
            }
        } else {
            AnimatedVisibility(visible = analysis != null) {
            analysis?.let { result ->
                Card(
                    modifier = Modifier.fillMaxWidth().border(1.dp, CyanAccent.copy(alpha=0.5f), RoundedCornerShape(16.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardBackground),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CheckCircle, null, tint = CyanAccent)
                            Spacer(Modifier.width(8.dp))
                            Text("Analysis Complete", color = CyanAccent, fontWeight = FontWeight.Bold)
                        }
                        Spacer(Modifier.height(16.dp))
                        
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            StatBox("Viral Score", "${result.viralScore}%", PurpleAccent)
                            StatBox("Est. Views", result.estimatedViews, PinkAccent)
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            StatBox("Engagement", result.engagementRate, CyanAccent)
                            StatBox("Best Time", result.bestUploadTime, GreenAccent)
                        }
                        
                        val instaData by viewModel.instaPostData.collectAsState()
                        if (instaData != null) {
                            Spacer(Modifier.height(16.dp))
                            Divider(color = Color.White.copy(alpha = 0.1f))
                            Spacer(Modifier.height(16.dp))
                            Text("Latest Instagram Post:", color = TextWhite, fontWeight = FontWeight.Bold)
                            Text(instaData ?: "", color = TextGray, fontSize = 12.sp, modifier = Modifier.padding(top = 4.dp))
                            
                            // Mock download button
                            Spacer(Modifier.height(16.dp))
                            Button(
                                onClick = { /* Future: File download implementation */ },
                                modifier = Modifier.height(36.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PinkAccent.copy(alpha=0.2f), contentColor = PinkAccent),
                                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 0.dp)
                            ) {
                                Icon(Icons.Default.Download, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Download Media", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
        }
    }
}

@Composable
fun StatBox(label: String, value: String, color: Color) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.White.copy(alpha = 0.05f))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(label, color = TextGray, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 4.dp))
        Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Black)
    }
}


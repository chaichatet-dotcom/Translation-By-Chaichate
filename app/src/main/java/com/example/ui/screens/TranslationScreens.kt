package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.CompareArrows
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.BasicTextField
import com.example.data.api.GeminiApiClient
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.TranslationEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.CameraScanState
import com.example.ui.viewmodel.TranslationViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Localization

// -------------------------------------------------------------
// SPLASH SCREEN (Fades automatically to Home or manual start)
// -------------------------------------------------------------
@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var animateLogo by remember { mutableStateOf(false) }
    val scaleFactor by animateFloatAsState(
        targetValue = if (animateLogo) 1.2f else 0.8f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow),
        label = "scale"
    )
    val opacityValue by animateFloatAsState(
        targetValue = if (animateLogo) 1f else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    val context = LocalContext.current
    val appLang = remember {
        val prefs = context.getSharedPreferences("smart_translate_prefs", android.content.Context.MODE_PRIVATE)
        val code = prefs.getString("ui_language_code", java.util.Locale.getDefault().language) ?: java.util.Locale.getDefault().language
        AppLanguage.fromCode(code)
    }

    LaunchedEffect(Unit) {
        animateLogo = true
        delay(2200) // Beautiful 2.2s intro
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Deep宇宙Background, Color(0xFF1B122B), Deep宇宙Background)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Futuristic background pattern
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = NeonIndigo.copy(alpha = 0.12f),
                radius = 400.dp.toPx(),
                center = Offset(size.width * 0.1f, size.height * 0.2f)
            )
            drawCircle(
                color = HologramPink.copy(alpha = 0.08f),
                radius = 300.dp.toPx(),
                center = Offset(size.width * 0.9f, size.height * 0.8f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .scale(scaleFactor)
                    .clip(RoundedCornerShape(24.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(BrightAzure, NeonIndigo)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Translate,
                    contentDescription = "App Logo",
                    modifier = Modifier.size(54.dp),
                    tint = Color.White
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Smart Translate AI",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                ),
                color = Color.White,
                modifier = Modifier.scale(scaleFactor)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = Localization.get("splash_tagline", appLang),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = TextSecondaryDark,
                    letterSpacing = 0.5.sp
                ),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            CircularProgressIndicator(
                color = BrightAzure,
                modifier = Modifier
                    .size(36.dp)
                    .testTag("splash_loading")
            )
        }
    }
}

// -------------------------------------------------------------
// HOME TRANSLATOR SCREEN
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: TranslationViewModel
) {
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    val appLang by viewModel.uiLanguage.collectAsState()
    val sourceText by viewModel.sourceText.collectAsState()
    val sourceLang by viewModel.sourceLang.collectAsState()
    val targetLang by viewModel.targetLang.collectAsState()
    val selectedTone by viewModel.selectedTone.collectAsState()
    val selectedMode by viewModel.selectedMode.collectAsState()
    val grammarCorrection by viewModel.grammarCorrection.collectAsState()
    val translationResult by viewModel.translationResult.collectAsState()
    val isTranslating by viewModel.isTranslating.collectAsState()
    val explanation by viewModel.linguisticExplanation.collectAsState()
    val isExplaining by viewModel.isExplaining.collectAsState()

    var showSourceMenu by remember { mutableStateOf(false) }
    var showTargetMenu by remember { mutableStateOf(false) }

    val languages = listOf("English", "Thai", "Hebrew", "Japanese", "Korean", "Chinese", "Russian", "Arabic", "Spanish")
    val tones = listOf("Natural", "Friendly", "Professional", "Casual", "Romantic", "Funny")

    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Deep宇宙Background)
    ) {
        // Accent Background glow
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                color = NeonIndigo.copy(alpha = 0.15f),
                radius = 350.dp.toPx(),
                center = Offset(size.width * 0.8f, size.height * 0.3f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = Localization.get("premium_ai", appLang),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 2.sp,
                            color = BrightAzure
                        ),
                        modifier = Modifier.padding(bottom = 2.dp)
                    )
                    Text(
                        text = "Smart Translate AI",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.SansSerif
                        ),
                        color = Color.White
                    )
                }
                
                // Live Glass Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.White.copy(alpha = 0.08f))
                        .border(1.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(6.dp)
                                .clip(CircleShape)
                                .background(BrightAzure)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Gemini AI",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Language selector row with glass effect
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberDarkCard.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Source Lang Selector
                    Box(modifier = Modifier.weight(1f)) {
                        TextButton(
                            onClick = { showSourceMenu = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("source_lang_btn")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = sourceLang,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondaryDark)
                            }
                        }
                        DropdownMenu(
                            expanded = showSourceMenu,
                            onDismissRequest = { showSourceMenu = false },
                            scrollState = rememberScrollState(),
                            modifier = Modifier.background(CyberDarkCard)
                        ) {
                            languages.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang, color = Color.White) },
                                    onClick = {
                                        viewModel.setSourceLang(lang)
                                        showSourceMenu = false
                                    }
                                )
                            }
                        }
                    }

                    // Swap Button
                    IconButton(
                        onClick = { viewModel.swapLanguages() },
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(CyberLightCard)
                            .testTag("swap_lang_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.CompareArrows,
                            contentDescription = "Swap Languages",
                            tint = BrightAzure
                        )
                    }

                    // Target Lang Selector
                    Box(modifier = Modifier.weight(1f)) {
                        TextButton(
                            onClick = { showTargetMenu = true },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("target_lang_btn")
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = targetLang,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Color.White,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1
                                )
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextSecondaryDark)
                            }
                        }
                        DropdownMenu(
                            expanded = showTargetMenu,
                            onDismissRequest = { showTargetMenu = false },
                            scrollState = rememberScrollState(),
                            modifier = Modifier.background(CyberDarkCard)
                        ) {
                            languages.forEach { lang ->
                                DropdownMenuItem(
                                    text = { Text(lang, color = Color.White) },
                                    onClick = {
                                        viewModel.setTargetLang(lang)
                                        showTargetMenu = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Main Large Text Input Card
            Card(
                colors = CardDefaults.cardColors(containerColor = CyberDarkCard.copy(alpha = 0.8f)),
                shape = RoundedCornerShape(24.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f)),
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        if (sourceText.isEmpty()) {
                            Text(
                                text = Localization.get("source_placeholder", appLang),
                                style = MaterialTheme.typography.bodyLarge.copy(color = TextSecondaryDark),
                                modifier = Modifier.padding(vertical = 4.dp)
                            )
                        }
                        BasicTextField(
                            value = sourceText,
                            onValueChange = { viewModel.setSourceText(it) },
                            textStyle = MaterialTheme.typography.bodyLarge.copy(color = Color.White, fontSize = 18.sp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight()
                                .testTag("translation_input"),
                            cursorBrush = Brush.verticalGradient(listOf(BrightAzure, NeonIndigo))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Clear Input Button
                            if (sourceText.isNotEmpty()) {
                                IconButton(
                                    onClick = { viewModel.clearInput() },
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear text", tint = TextSecondaryDark)
                                }
                            }
                            // Speech simulation shortcuts trigger
                            IconButton(
                                onClick = { 
                                    viewModel.setSourceText("Hey mate, I was absolutely famished. Down to grab some quick bites?") 
                                },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(Icons.Default.TipsAndUpdates, contentDescription = "Simulate Slang Suggestion", tint = BrightAzure)
                            }
                        }

                        Text(
                            text = "${sourceText.length}/1000",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondaryDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Grammar pre-correction Switch Checkbox
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { viewModel.toggleGrammar() }
                    .background(if (grammarCorrection) BrightAzure.copy(alpha = 0.1f) else Color.Transparent)
                    .border(
                        1.dp,
                        if (grammarCorrection) BrightAzure.copy(alpha = 0.4f) else Color.Transparent,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Spellcheck,
                        contentDescription = null,
                        tint = if (grammarCorrection) BrightAzure else TextSecondaryDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Localization.get("auto_correct_grammar", appLang),
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (grammarCorrection) Color.White else TextSecondaryDark
                    )
                }
                Switch(
                    checked = grammarCorrection,
                    onCheckedChange = { viewModel.toggleGrammar() },
                    colors = SwitchDefaults.colors(checkedThumbColor = BrightAzure, checkedTrackColor = BrightAzure.copy(alpha = 0.3f))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. GPT-Style Mode Choice Selector (Direct Translation vs AI Natural)
            Text(
                text = Localization.get("translation_engine_mode", appLang),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = TextSecondaryDark,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(CyberDarkCard)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val modes = listOf("AI Natural", "Direct")
                modes.forEach { mode ->
                    val isSelected = selectedMode == mode
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .let { mod ->
                                if (isSelected) {
                                    mod.background(Brush.horizontalGradient(listOf(BrightAzure, NeonIndigo)))
                                } else {
                                    mod.background(Color.Transparent)
                                }
                            }
                            .clickable { viewModel.setMode(mode) }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (mode == "Direct") Icons.Default.Gavel else Icons.Default.Casino,
                                contentDescription = null,
                                tint = if (isSelected) Color.White else TextSecondaryDark,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (mode == "Direct") Localization.get("direct_mode", appLang) else Localization.get("ai_natural_mode", appLang),
                                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                color = if (isSelected) Color.White else TextSecondaryDark
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Tone Adjustment Row
            Text(
                text = Localization.get("tone_selector", appLang),
                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                color = TextSecondaryDark,
                modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(tones) { tone ->
                    val isSelected = selectedTone == tone
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                if (isSelected) BrightAzure.copy(alpha = 0.2f) else CyberDarkCard
                            )
                            .border(
                                1.dp,
                                if (isSelected) BrightAzure else Color.White.copy(alpha = 0.1f),
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { viewModel.setTone(tone) }
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tone,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                            color = if (isSelected) Color.White else TextSecondaryDark
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Translate Glowing Button
            Button(
                onClick = { viewModel.performAiTranslation() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .testTag("ai_translate_button"),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.horizontalGradient(
                                colors = listOf(BrightAzure, NeonIndigo)
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isTranslating) {
                        CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                    } else {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Generate Natural AI Translation",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.ExtraBold),
                                color = Color.White
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 5. Output Card with dynamic visible animation
            AnimatedVisibility(
                visible = translationResult.isNotEmpty() || isTranslating,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    shape = RoundedCornerShape(24.dp),
                    border = BorderStroke(1.dp, Color(0x4022D3EE)), // cyan-400/25 border
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0x59164E63), Color(0x591E3A8A)) // cyan-900/35 to blue-900/35
                            ),
                            shape = RoundedCornerShape(24.dp)
                        )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Translated Result ($targetLang)",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = BrightAzure)
                            )

                            // Favorite Button
                            IconButton(
                                onClick = {
                                    viewModel.toggleFavorite(
                                        TranslationEntity(
                                            sourceText = sourceText,
                                            targetText = translationResult,
                                            sourceLang = sourceLang,
                                            targetLang = targetLang,
                                            tone = selectedTone,
                                            mode = selectedMode
                                        )
                                    )
                                    Toast.makeText(context, "Added to local bookmarks!", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.testTag("save_fav_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.FavoriteBorder,
                                    contentDescription = "Favorite translation",
                                    tint = HologramPink
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Output translated content
                        Text(
                            text = if (translationResult.isEmpty() && isTranslating) "Synthesizing conversational nuances..." else translationResult,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = if (translationResult.isEmpty() && isTranslating) TextSecondaryDark else Color.White,
                                lineHeight = 26.sp,
                                fontSize = 19.sp
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("result_text")
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Output toolbar: Copy, Share, Audio Voice Speak & Deep explanation
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                // Speak Result aloud natively
                                IconButton(
                                    onClick = { viewModel.speakOutput(translationResult, targetLang) },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), CircleShape)
                                        .testTag("speak_output_btn")
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Listen voice", tint = Color.White)
                                }

                                // Copy Button
                                IconButton(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(translationResult))
                                        Toast.makeText(context, "Copied translation!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), CircleShape)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy text", tint = Color.White)
                                }

                                // Share Button
                                IconButton(
                                    onClick = {
                                        Toast.makeText(context, "Ready to share!", Toast.LENGTH_SHORT).show()
                                    },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color.White.copy(alpha = 0.08f))
                                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), CircleShape)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = "Share", tint = Color.White)
                                }
                            }

                            // Smart AI Phrase analysis explanation button
                            TextButton(
                                onClick = { viewModel.getLinguisticExplanation() },
                                colors = ButtonDefaults.textButtonColors(contentColor = BrightAzure)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Analyze Phrase Nuance", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold))
                                }
                            }
                        }

                        // Linguistic analysis card drawer
                        AnimatedVisibility(
                            visible = isExplaining || explanation.isNotEmpty(),
                            enter = fadeIn() + expandVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 16.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.05f))
                                    .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)), RoundedCornerShape(16.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.School, contentDescription = null, tint = HologramPink, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text("Cultural & Linguistic Breakdown", style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                                }
                                Spacer(modifier = Modifier.height(8.dp))
                                if (isExplaining && explanation.isEmpty()) {
                                    CircularProgressIndicator(color = HologramPink, modifier = Modifier.size(20.dp).align(Alignment.CenterHorizontally))
                                } else {
                                    Text(
                                        text = explanation,
                                        style = MaterialTheme.typography.bodyMedium.copy(color = TextPrimaryDark, lineHeight = 20.sp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// CHAT TRANSLATOR SCREEN (ChatGPT-style dialogue assistant)
// -------------------------------------------------------------
@Composable
fun ChatScreen(
    viewModel: TranslationViewModel
) {
    val sourceLang by viewModel.sourceLang.collectAsState()
    val targetLang by viewModel.targetLang.collectAsState()

    var chatInput by remember { mutableStateOf("") }
    var chatHistory by remember { mutableStateOf(listOf(
        ChatMessage(
            id = 1,
            text = "Welcome! I am your Smart Translating Assistant. Send me any text, slang, or idioms to translate from $sourceLang to $targetLang using hyper-natural nuances.",
            sender = "assistant"
        )
    )) }
    var isBotThinking by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Deep宇宙Background)
            .navigationBarsPadding()
            .imePadding()
    ) {
        // Chat Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberDarkCard)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(BrightAzure, NeonIndigo))),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("AI Nuance Buddy", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                    Text("Linguistic flow $sourceLang → $targetLang", style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)
                }
            }
        }

        // Messages Box Scrollable
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(chatHistory) { msg ->
                    val isUser = msg.sender == "user"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.82f)
                                .clip(
                                    RoundedCornerShape(
                                        topStart = 16.dp, 
                                        topEnd = 16.dp, 
                                        bottomStart = if (isUser) 16.dp else 4.dp, 
                                        bottomEnd = if (isUser) 4.dp else 16.dp
                                    )
                                )
                                .background(if (isUser) NeonIndigo else CyberDarkCard)
                                .border(1.dp, if (isUser) Color.Transparent else Color.White.copy(alpha = 0.05f), RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text(
                                    text = msg.text,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (isUser) "You" else "Smart Translate AI Bot",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                                    color = if (isUser) Color.White.copy(alpha = 0.7f) else BrightAzure
                                )
                            }
                        }
                    }
                }
                
                if (isBotThinking) {
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(CyberDarkCard)
                                    .padding(12.dp)
                            ) {
                                Text("Translating with context nuances...", style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)
                            }
                        }
                    }
                }
            }
        }

        // Bottom input row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberDarkCard)
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextField(
                value = chatInput,
                onValueChange = { chatInput = it },
                placeholder = { Text("Send colloquial sentence...", color = TextSecondaryDark) },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Deep宇宙Background,
                    unfocusedContainerColor = Deep宇宙Background,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input"),
                maxLines = 3
            )

            Spacer(modifier = Modifier.width(8.dp))

            IconButton(
                onClick = {
                    val content = chatInput.trim()
                    if (content.isEmpty()) return@IconButton
                    
                    chatInput = ""
                    val userMsg = ChatMessage(id = chatHistory.size + 1, text = content, sender = "user")
                    chatHistory = chatHistory + userMsg
                    isBotThinking = true

                    scope.launch {
                        var streamingVal = ""
                        GeminiApiClient.translateStream(
                            text = content,
                            sourceLang = sourceLang,
                            targetLang = targetLang,
                            mode = "AI Natural",
                            tone = "Natural"
                        ).collect { chunk: String ->
                            streamingVal += chunk
                        }

                        isBotThinking = false
                        chatHistory = chatHistory + ChatMessage(
                            id = chatHistory.size + 2,
                            text = "Translated ($targetLang):\n\n$streamingVal\n\n💡 I chose these words to keep the friendly, natural atmosphere intact while matching local slang perfectly.",
                            sender = "assistant"
                        )
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(BrightAzure)
                    .testTag("chat_send_btn")
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send text", tint = Color.White)
            }
        }
    }
}

data class ChatMessage(val id: Int, val text: String, val sender: String)

// -------------------------------------------------------------
// VOICE TRANSLATION SCREEN (Real-time Speech visualization)
// -------------------------------------------------------------
@Composable
fun VoiceScreen(
    viewModel: TranslationViewModel
) {
    val sourceLang by viewModel.sourceLang.collectAsState()
    val targetLang by viewModel.targetLang.collectAsState()
    val recordingState by viewModel.voiceRecordingState.collectAsState()
    val finalResult by viewModel.translationResult.collectAsState()
    val isTranslating by viewModel.isTranslating.collectAsState()

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Pulsing animation for audio waves
    val infiniteTransition = rememberInfiniteTransition()
    val rippleScale by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = if (recordingState) 1.5f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Deep宇宙Background)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Title Info
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(30.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = BrightAzure)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Voice AI Translation", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold), color = Color.White)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("Auto translating spoken $sourceLang directly to $targetLang", style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark, textAlign = TextAlign.Center)
        }

        // Large recording visualization ring
        Box(
            modifier = Modifier
                .size(240.dp)
                .clip(CircleShape)
                .drawBehind {
                    // Draw flowing outer glowing aura
                    drawCircle(
                        color = if (recordingState) BrightAzure.copy(alpha = 0.15f) else NeonIndigo.copy(alpha = 0.1f),
                        radius = (size.minDimension / 2) * rippleScale
                    )
                },
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                IconButton(
                    onClick = {
                        if (!recordingState) {
                            viewModel.startVoiceRecordingSim()
                            scope.launch {
                                delay(3000) // Simulate user talking for 3 sec
                                // Pick up a random funny spoken slang depending on standard source
                                val phrases = listOf(
                                    "I have a lot on my plate, can we talk tomorrow?",
                                    "Break a leg at your presentation today!",
                                    "This is getting out of hand."
                                )
                                viewModel.stopVoiceRecordingAndSimulate(phrases.random())
                            }
                        }
                    },
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = if (recordingState) listOf(HologramPink, NeonIndigo) else listOf(BrightAzure, NeonIndigo)
                            )
                        )
                        .testTag("mic_overlay_trigger")
                ) {
                    Icon(
                        imageVector = if (recordingState) Icons.Default.Hearing else Icons.Default.Mic,
                        contentDescription = "Trigger Speech",
                        tint = Color.White,
                        modifier = Modifier.size(54.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (recordingState) "Listening Spoken Flow..." else "Tap to Speak",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = if (recordingState) HologramPink else Color.White
                )
            }
        }

        // Output Result card
        Column(
            modifier = Modifier.fillMaxWidth().weight(0.4f),
            verticalArrangement = Arrangement.Bottom
        ) {
            if (isTranslating) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberDarkCard),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth().padding(16.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = BrightAzure)
                    }
                }
            } else if (finalResult.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = CyberLightCard),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    border = BorderStroke(1.dp, BrightAzure.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Instant Translation To $targetLang", style = MaterialTheme.typography.labelSmall.copy(color = BrightAzure, fontWeight = FontWeight.Bold))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(finalResult, style = MaterialTheme.typography.bodyLarge.copy(color = Color.White))
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            IconButton(
                                onClick = { viewModel.speakOutput(finalResult, targetLang) },
                                modifier = Modifier.clip(CircleShape).background(Deep宇宙Background)
                            ) {
                                Icon(Icons.Default.VolumeUp, contentDescription = "Listen voice translation", tint = Color.White)
                            }
                        }
                    }
                }
            } else {
                Text(
                    "Simply press the microphone, speak an idiom, and watch Smart AI generate a perfect colloquial translation in real-time.",
                    style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondaryDark, lineHeight = 20.sp),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

// -------------------------------------------------------------
// CAMERA TRANSLATION SCREEN (Mock scanning and file OCR)
// -------------------------------------------------------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CameraScreen(
    viewModel: TranslationViewModel
) {
    val sourceLang by viewModel.sourceLang.collectAsState()
    val targetLang by viewModel.targetLang.collectAsState()
    val scanState by viewModel.cameraScanningResult.collectAsState()
    val translationResult by viewModel.translationResult.collectAsState()
    val isTranslating by viewModel.isTranslating.collectAsState()

    // Slide line scanner overlay animation
    val infiniteTransition = rememberInfiniteTransition()
    val scannerBeamY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Deep宇宙Background)
            .padding(16.dp)
    ) {
        // Upper Title
        Spacer(modifier = Modifier.height(12.dp))
        Text("AI Optical Lens", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
        Text("Scan signs, menus, or screenshots to translate", style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)

        Spacer(modifier = Modifier.height(16.dp))

        // Large camera viewport container
        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            border = BorderStroke(2.dp, if (scanState is CameraScanState.Scanning) BrightAzure else Color.White.copy(alpha = 0.15f))
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Background simulated camera lens crosshairs and lines
                Canvas(modifier = Modifier.fillMaxSize()) {
                    // Frame lines
                    val p = 16.dp.toPx()
                    val l = 32.dp.toPx()
                    // Top-Left corner indicators
                    drawLine(Color.White, Offset(p, p), Offset(p + l, p), strokeWidth = 3.dp.toPx())
                    drawLine(Color.White, Offset(p, p), Offset(p, p + l), strokeWidth = 3.dp.toPx())
                    // Top-Right
                    drawLine(Color.White, Offset(size.width - p, p), Offset(size.width - p - l, p), strokeWidth = 3.dp.toPx())
                    drawLine(Color.White, Offset(size.width - p, p), Offset(size.width - p, p + l), strokeWidth = 3.dp.toPx())
                    // Bottom-Left
                    drawLine(Color.White, Offset(p, size.height - p), Offset(p + l, size.height - p), strokeWidth = 3.dp.toPx())
                    drawLine(Color.White, Offset(p, size.height - p), Offset(p, size.height - p - l), strokeWidth = 3.dp.toPx())
                    // Bottom-Right
                    drawLine(Color.White, Offset(size.width - p, size.height - p), Offset(size.width - p - l, size.height - p), strokeWidth = 3.dp.toPx())
                    drawLine(Color.White, Offset(size.width - p, size.height - p), Offset(size.width - p, size.height - p - l), strokeWidth = 3.dp.toPx())
                }

                // If scanning, draw glowing green screen beam slider
                if (scanState is CameraScanState.Scanning) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight(0.04f)
                            .align(Alignment.TopCenter)
                            .offset(y = 350.dp * scannerBeamY)
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, BrightAzure.copy(alpha = 0.8f), Color.Transparent)
                                )
                            )
                    )
                }

                // Centered dynamic status labels
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.Center)
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    when (scanState) {
                        is CameraScanState.Idle -> {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, size = 64.dp, tint = Color.White.copy(alpha = 0.3f))
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Awaiting Document Viewport", color = Color.White.copy(alpha = 0.6f), fontWeight = FontWeight.SemiBold)
                            Text("Center text inside the camera guidelines", color = TextSecondaryDark, fontSize = 13.sp)
                        }
                        is CameraScanState.Scanning -> {
                            Icon(Icons.Default.FlipCameraAndroid, contentDescription = null, size = 64.dp, tint = BrightAzure)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("Running Optical Character Recognition...", color = BrightAzure, fontWeight = FontWeight.Bold)
                            Text("Gemini is reading linguistic blocks", color = TextSecondaryDark)
                        }
                        is CameraScanState.Completed -> {
                            Icon(Icons.Default.DocumentScanner, contentDescription = null, size = 50.dp, tint = BrightAzure)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text("OCR Scan Completed successfully!", color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Extracted text:", color = TextSecondaryDark)
                            Box(
                                modifier = Modifier
                                    .padding(top = 8.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(CyberDarkCard)
                                    .padding(8.dp)
                            ) {
                                Text((scanState as CameraScanState.Completed).textScanned, color = Color.White, fontSize = 14.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Control Panel
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Pick image simulation
            IconButton(
                onClick = { viewModel.startCameraMockScan() },
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(CyberDarkCard)
            ) {
                Icon(Icons.Default.PhotoLibrary, contentDescription = "Gallery Mock", tint = Color.White)
            }

            // Big Shutter trigger
            IconButton(
                onClick = { viewModel.startCameraMockScan() },
                modifier = Modifier
                    .size(76.dp)
                    .clip(CircleShape)
                    .background(Brush.linearGradient(listOf(BrightAzure, NeonIndigo)))
                    .testTag("shutter_btn")
            ) {
                Icon(Icons.Default.CameraAlt, contentDescription = "Scan Screen", tint = Color.White, modifier = Modifier.size(36.dp))
            }

            // Reset Camera
            IconButton(
                onClick = { viewModel.resetCameraScan() },
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(CyberDarkCard)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Clear Camera State", tint = Color.White)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

// -------------------------------------------------------------
// CONVERSATION MODE SCREEN (Split head-to-head live translator)
// -------------------------------------------------------------
@Composable
fun ConversationScreen(
    viewModel: TranslationViewModel
) {
    val sourceLang by viewModel.sourceLang.collectAsState()
    val targetLang by viewModel.targetLang.collectAsState()
    val history by viewModel.conversationHistory.collectAsState()

    val topRecording by viewModel.topPersonRecording.collectAsState()
    val bottomRecording by viewModel.bottomPersonRecording.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Deep宇宙Background)
    ) {
        // Topic info board
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CyberDarkCard)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("Live Dual Conversation", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                Text("Bilingual screen split mode", style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)
            }
            IconButton(onClick = { viewModel.clearConversations() }) {
                Icon(Icons.Default.DeleteOutline, contentDescription = "Clear dialogues", tint = TextSecondaryDark)
            }
        }

        // Split Layout Grid
        Column(modifier = Modifier.weight(1f).fillMaxWidth()) {
            // TOP SPEAKER ZONE (Host Language, rotated 180 deg so they face each other easily!)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(BorderStroke(0.5.dp, Color.White.copy(alpha = 0.08f)))
                    .background(CyberDarkCard.copy(alpha = 0.7f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "SPEAKER A ($sourceLang)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = BrightAzure, letterSpacing = 1.sp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    IconButton(
                        onClick = { viewModel.toggleTopConversationRecording() },
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(if (topRecording) HologramPink else BrightAzure)
                            .testTag("top_speaker_mic")
                    ) {
                        Icon(
                            imageVector = if (topRecording) Icons.Default.Hearing else Icons.Default.Mic,
                            contentDescription = "Speaker A mic",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (topRecording) "Listening..." else "Tap to talk ($sourceLang)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (topRecording) HologramPink else Color.White
                    )
                }
            }

            // MIDDLE LIVE TRANSCRIPT FEED WINDOW
            Box(
                modifier = Modifier
                    .weight(1.5f)
                    .fillMaxWidth()
                    .background(Deep宇宙Background)
                    .padding(horizontal = 16.dp)
            ) {
                if (history.isEmpty()) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            "Each speaker taps their microphone button to begin. Smart AI parses and speaks responses automatically.",
                            color = TextSecondaryDark,
                            textAlign = TextAlign.Center,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(history) { utterance ->
                            val isTop = utterance.sender == "top"
                            Card(
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isTop) NeonIndigo.copy(alpha = 0.15f) else BrightAzure.copy(alpha = 0.15f)
                                ),
                                shape = RoundedCornerShape(16.dp),
                                border = BorderStroke(1.dp, if (isTop) NeonIndigo.copy(alpha = 0.4f) else BrightAzure.copy(alpha = 0.4f)),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(
                                        text = if (isTop) "Speaker A -> to ${utterance.toLang}" else "Speaker B -> to ${utterance.toLang}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = if (isTop) Purple80 else BrightAzure)
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(text = "Original: ${utterance.originalText}", style = MaterialTheme.typography.bodyMedium, color = Color.White)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "AI Natural: ${utterance.translatedText}",
                                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold, color = Color.White)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // BOTTOM SPEAKER ZONE (Local Destination Language)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .border(BorderStroke(0.5.dp, Color.White.copy(alpha = 0.08f)))
                    .background(CyberDarkCard.copy(alpha = 0.7f))
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "SPEAKER B ($targetLang)",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = NeonIndigo, letterSpacing = 1.sp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    IconButton(
                        onClick = { viewModel.toggleBottomConversationRecording() },
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(if (bottomRecording) HologramPink else NeonIndigo)
                            .testTag("bottom_speaker_mic")
                    ) {
                        Icon(
                            imageVector = if (bottomRecording) Icons.Default.Hearing else Icons.Default.Mic,
                            contentDescription = "Speaker B mic",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (bottomRecording) "Listening..." else "Tap to talk ($targetLang)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (bottomRecording) HologramPink else Color.White
                    )
                }
            }
        }
    }
}

// -------------------------------------------------------------
// HISTORY & RECENT TRANSLATION ARCHIVE
// -------------------------------------------------------------
@Composable
fun HistoryScreen(
    viewModel: TranslationViewModel
) {
    val history by viewModel.allTranslations.collectAsState()
    val favorites by viewModel.favoriteTranslations.collectAsState()
    val currentLang by viewModel.uiLanguage.collectAsState()

    var showFavoritesOnly by remember { mutableStateOf(false) }
    val displayList = if (showFavoritesOnly) favorites else history

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Deep宇宙Background)
            .padding(16.dp)
    ) {
        // Tab Headers
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(Localization.get("recent_logs", currentLang), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
                Text(Localization.get("commit_logs_descr", currentLang), style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)
            }
            if (history.isNotEmpty()) {
                TextButton(onClick = { viewModel.clearAllHistory() }) {
                    Text(Localization.get("clear_history", currentLang), color = HologramPink, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // History Categories Filter row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(CyberDarkCard)
                .padding(4.dp)
        ) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (!showFavoritesOnly) BrightAzure else Color.Transparent)
                    .clickable { showFavoritesOnly = false }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("All Cache (${history.size})", fontWeight = FontWeight.Bold, color = Color.White)
            }
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (showFavoritesOnly) BrightAzure else Color.Transparent)
                    .clickable { showFavoritesOnly = true }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Favs (${favorites.size})", fontWeight = FontWeight.Bold, color = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Display logs
        if (displayList.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = if (showFavoritesOnly) Icons.Default.StarBorder else Icons.Default.HistoryToggleOff,
                        contentDescription = null,
                        size = 64.dp,
                        tint = TextSecondaryDark.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (showFavoritesOnly) "Bookmarks are empty" else Localization.get("no_saved_translations", currentLang),
                        color = Color.White.copy(alpha = 0.6f),
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "Your AI translations will persist safe right here",
                        color = TextSecondaryDark,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 24.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(displayList) { translation ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = CyberDarkCard),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth(),
                        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.04f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${translation.sourceLang} → ${translation.targetLang}",
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = BrightAzure)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(CyberLightCard)
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        Text(translation.tone, color = TextSecondaryDark, fontSize = 9.sp)
                                    }
                                }
                                
                                Row {
                                    IconButton(
                                        onClick = { viewModel.toggleFavorite(translation) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (translation.isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = "Toggle bookmark",
                                            tint = if (translation.isFavorite) HologramPink else TextSecondaryDark
                                        )
                                    }
                                    IconButton(
                                        onClick = { viewModel.deleteTranslation(translation.id) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(Icons.Default.DeleteOutline, contentDescription = "Delete entry", tint = TextSecondaryDark)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = translation.sourceText, style = MaterialTheme.typography.bodyMedium, color = Color.White)
                            Spacer(modifier = Modifier.height(6.dp))
                            Divider(color = Color.White.copy(alpha = 0.05f))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(text = translation.targetText, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = Color.White)

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                                IconButton(
                                    onClick = { viewModel.speakOutput(translation.targetText, translation.targetLang) },
                                    modifier = Modifier.size(32.dp).clip(CircleShape).background(CyberLightCard)
                                ) {
                                    Icon(Icons.Default.VolumeUp, contentDescription = "Listen back", tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// -------------------------------------------------------------
// SETTINGS PREFERENCES SCREEN
// -------------------------------------------------------------
@Composable
fun SettingsScreen(
    viewModel: TranslationViewModel
) {
    val rateSpeed by viewModel.speechRateMultiplier.collectAsState()
    val offlineCache by viewModel.offlineCacheEnabled.collectAsState()
    val currentLang by viewModel.uiLanguage.collectAsState()

    var expanded by remember { mutableStateOf(false) }
    var selectedTempLang by remember(currentLang) { mutableStateOf(currentLang) }

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Deep宇宙Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))
        Text(Localization.get("system_preferences", currentLang), style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = Color.White)
        Text(Localization.get("fine_tune_cognitive", currentLang), style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)

        Spacer(modifier = Modifier.height(16.dp))

        // UI Language Selector Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberDarkCard),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Language, contentDescription = null, tint = HologramPink)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = Localization.get("ui_language", currentLang),
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = Localization.get("ui_language_descr", currentLang),
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondaryDark
                )
                Spacer(modifier = Modifier.height(16.dp))

                // Selector Box with dropdown menu
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { expanded = true }
                        .background(Color.White.copy(alpha = 0.05f), RoundedCornerShape(12.dp))
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.12f)), RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 14.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${selectedTempLang.displayName} (${selectedTempLang.nativeName})",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = Color.White)
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.85f)
                            .background(Deep宇宙Background)
                            .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.15f)), RoundedCornerShape(12.dp))
                    ) {
                        AppLanguage.values().forEach { lang ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "${lang.displayName} (${lang.nativeName})",
                                            color = if (lang == selectedTempLang) BrightAzure else Color.White
                                        )
                                        if (lang == currentLang) {
                                            Text(
                                                text = Localization.get("active", currentLang),
                                                color = BrightAzure,
                                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    selectedTempLang = lang
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Preview Label
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White.copy(alpha = 0.03f), RoundedCornerShape(12.dp))
                        .border(BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Column {
                        Text(
                            text = Localization.get("language_preview", currentLang),
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = HologramPink)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // Real-time language specific UI sample
                        Text(
                            text = Localization.get("splash_tagline", selectedTempLang),
                            style = MaterialTheme.typography.bodyLarge.copy(fontStyle = androidx.compose.ui.text.font.FontStyle.Italic),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Apply button
                Button(
                    onClick = {
                        viewModel.selectUiLanguage(selectedTempLang)
                        Toast.makeText(context, Localization.get("language_applied", selectedTempLang), Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier.fillMaxWidth().testTag("apply_language_btn"),
                    enabled = selectedTempLang != currentLang,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonIndigo,
                        disabledContainerColor = Color.White.copy(alpha = 0.08f)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = Localization.get("apply_lang", currentLang),
                        color = if (selectedTempLang != currentLang) Color.White else Color.White.copy(alpha = 0.4f),
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Vocal TTS speed slider
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberDarkCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = BrightAzure)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(Localization.get("vocal_speech_rate", currentLang), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = Color.White)
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(Localization.get("vocal_speech_descr", currentLang), style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)
                Spacer(modifier = Modifier.height(12.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Slider(
                        value = rateSpeed,
                        onValueChange = { viewModel.updateSpeechRate(it) },
                        valueRange = 0.5f..2.0f,
                        modifier = Modifier.weight(1f).testTag("speech_rate_slider")
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("${"%.1f".format(rateSpeed)}x", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Offsite cache toggle switch
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberDarkCard),
            shape = RoundedCornerShape(16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.toggleOfflineCache(!offlineCache) }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(Icons.Default.CloudQueue, contentDescription = null, tint = NeonIndigo)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(Localization.get("persistent_offline_cache", currentLang), style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), color = Color.White)
                        Text(Localization.get("commit_logs_descr", currentLang), style = MaterialTheme.typography.bodyMedium, color = TextSecondaryDark)
                    }
                }
                Switch(
                    checked = offlineCache,
                    onCheckedChange = { viewModel.toggleOfflineCache(it) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // System specs card
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberLightCard.copy(alpha = 0.6f)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(Localization.get("linguistic_core_details", currentLang), style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = BrightAzure))
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(Localization.get("translating_engine_model", currentLang), color = TextSecondaryDark, fontSize = 13.sp)
                    Text("Google Gemini-3.5-flash", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(Localization.get("api_service_key_protocol", currentLang), color = TextSecondaryDark, fontSize = 13.sp)
                    Text("BuildConfig SECRETS Encrypted", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(Localization.get("software_arch_version", currentLang), color = TextSecondaryDark, fontSize = 13.sp)
                    Text("v1.5.0 Premium Hybrid", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Utility icon helper for compilation safety
@Composable
private fun Icon(imageVector: androidx.compose.ui.graphics.vector.ImageVector, contentDescription: String?, size: androidx.compose.ui.unit.Dp, tint: Color) {
    androidx.compose.material3.Icon(imageVector = imageVector, contentDescription = contentDescription, modifier = Modifier.size(size), tint = tint)
}

package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.localization.AppLanguage
import com.example.ui.localization.Localization
import com.example.ui.screens.*
import com.example.ui.theme.BrightAzure
import com.example.ui.theme.CyberDarkCard
import com.example.ui.theme.Deep宇宙Background
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.TranslationViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var showSplash by remember { mutableStateOf(true) }

                if (showSplash) {
                    SplashScreen(onSplashFinished = { showSplash = false })
                } else {
                    MainAppContent()
                }
            }
        }
    }
}

@Composable
fun MainAppContent() {
    val viewModel: TranslationViewModel = viewModel()
    val activeTab by viewModel.currentTab.collectAsState()
    val appLang by viewModel.uiLanguage.collectAsState()
    val layoutDirection = if (appLang.isRtl) LayoutDirection.Rtl else LayoutDirection.Ltr

    CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
        // Inner toggle state inside the primary Bottom Bar grouping tabs
        // Voice sub-tab: "solo" vs "split"
        var voiceMode by remember { mutableStateOf("solo") }
        // Archive sub-tab: "history" vs "settings"
        var archiveMode by remember { mutableStateOf("history") }

        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .testTag("main_scaffold"),
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                NavigationBar(
                    containerColor = CyberDarkCard,
                    tonalElevation = 8.dp,
                    modifier = Modifier
                        .windowInsetsPadding(WindowInsets.navigationBars)
                        .drawBehind {
                            drawLine(
                                color = Color.White.copy(alpha = 0.08f),
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                        .testTag("bottom_nav_bar")
                ) {
                    // 1. Translate
                    NavigationBarItem(
                        selected = activeTab == "home",
                        onClick = { viewModel.selectTab("home") },
                        icon = { Icon(Icons.Default.Translate, contentDescription = "Translate Home") },
                        label = { Text(Localization.get("nav_translate", appLang), fontSize = 11.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = BrightAzure,
                            indicatorColor = BrightAzure.copy(alpha = 0.15f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_home_tab")
                    )

                    // 2. Chat Buddy
                    NavigationBarItem(
                        selected = activeTab == "chat",
                        onClick = { viewModel.selectTab("chat") },
                        icon = { Icon(Icons.Default.AutoAwesome, contentDescription = "AI Chat") },
                        label = { Text(Localization.get("nav_chat", appLang), fontSize = 11.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = BrightAzure,
                            indicatorColor = BrightAzure.copy(alpha = 0.15f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_chat_tab")
                    )

                    // 3. Voice (Toggles Solo vs split conversation)
                    NavigationBarItem(
                        selected = activeTab == "voice",
                        onClick = { viewModel.selectTab("voice") },
                        icon = { Icon(Icons.Default.Mic, contentDescription = "Voice Translation") },
                        label = { Text(Localization.get("nav_voice", appLang), fontSize = 11.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = BrightAzure,
                            indicatorColor = BrightAzure.copy(alpha = 0.15f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_voice_tab")
                    )

                    // 4. Lens OCR
                    NavigationBarItem(
                        selected = activeTab == "camera",
                        onClick = { viewModel.selectTab("camera") },
                        icon = { Icon(Icons.Default.CameraAlt, contentDescription = "OCR Lens") },
                        label = { Text(Localization.get("nav_camera", appLang), fontSize = 11.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = BrightAzure,
                            indicatorColor = BrightAzure.copy(alpha = 0.15f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_camera_tab")
                    )

                    // 5. Cache / Archive (History and Settings)
                    NavigationBarItem(
                        selected = activeTab == "archive",
                        onClick = { viewModel.selectTab("archive") },
                        icon = { Icon(Icons.Default.History, contentDescription = "Archive logs") },
                        label = { Text(Localization.get("nav_archive", appLang), fontSize = 11.sp, maxLines = 1) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = BrightAzure,
                            indicatorColor = BrightAzure.copy(alpha = 0.15f),
                            unselectedIconColor = Color.Gray,
                            unselectedTextColor = Color.Gray
                        ),
                        modifier = Modifier.testTag("nav_archive_tab")
                    )
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Deep宇宙Background)
                    .padding(innerPadding)
            ) {
                when (activeTab) {
                    "home" -> {
                        HomeScreen(viewModel = viewModel)
                    }
                    "chat" -> {
                        ChatScreen(viewModel = viewModel)
                    }
                    "voice" -> {
                        // Split panel for voice options
                        Column(modifier = Modifier.fillMaxSize()) {
                            TabRow(
                                selectedTabIndex = if (voiceMode == "solo") 0 else 1,
                                containerColor = CyberDarkCard,
                                contentColor = BrightAzure
                            ) {
                                Tab(
                                    selected = voiceMode == "solo",
                                    onClick = { voiceMode = "solo" },
                                    text = { Text(Localization.get("solo_interpreter", appLang), color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1) },
                                    icon = { Icon(Icons.Default.VolumeUp, contentDescription = null, tint = BrightAzure) }
                                )
                                Tab(
                                    selected = voiceMode == "split",
                                    onClick = { voiceMode = "split" },
                                    text = { Text(Localization.get("duo_conversation", appLang), color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1) },
                                    icon = { Icon(Icons.Default.Forum, contentDescription = null, tint = BrightAzure) }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                if (voiceMode == "solo") {
                                    VoiceScreen(viewModel = viewModel)
                                } else {
                                    ConversationScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                    "camera" -> {
                        CameraScreen(viewModel = viewModel)
                    }
                    "archive" -> {
                        Column(modifier = Modifier.fillMaxSize()) {
                            TabRow(
                                selectedTabIndex = if (archiveMode == "history") 0 else 1,
                                containerColor = CyberDarkCard,
                                contentColor = BrightAzure
                            ) {
                                Tab(
                                    selected = archiveMode == "history",
                                    onClick = { archiveMode = "history" },
                                    text = { Text(Localization.get("recent_logs", appLang), color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1) },
                                    icon = { Icon(Icons.Default.History, contentDescription = null, tint = BrightAzure) }
                                )
                                Tab(
                                    selected = archiveMode == "settings",
                                    onClick = { archiveMode = "settings" },
                                    text = { Text(Localization.get("preferences", appLang), color = Color.White, fontWeight = FontWeight.SemiBold, maxLines = 1) },
                                    icon = { Icon(Icons.Default.Settings, contentDescription = null, tint = BrightAzure) }
                                )
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                if (archiveMode == "history") {
                                    HistoryScreen(viewModel = viewModel)
                                } else {
                                    SettingsScreen(viewModel = viewModel)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

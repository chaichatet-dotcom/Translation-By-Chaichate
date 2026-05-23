package com.example.ui.viewmodel

import android.app.Application
import android.speech.tts.TextToSpeech
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.api.GeminiApiClient
import com.example.data.db.AppDatabase
import com.example.data.db.TranslationEntity
import com.example.data.repository.TranslationRepository
import com.example.ui.localization.AppLanguage
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale

class TranslationViewModel(application: Application) : AndroidViewModel(application), TextToSpeech.OnInitListener {

    private val repository: TranslationRepository
    private var tts: TextToSpeech? = null
    private var isTtsInitialized = false

    private val _uiLanguage = MutableStateFlow(AppLanguage.ENGLISH)
    val uiLanguage: StateFlow<AppLanguage> = _uiLanguage.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = TranslationRepository(database.translationDao())
        
        // Load UI language preferences with system auto-detect fallback
        val sharedPrefs = application.getSharedPreferences("smart_translate_prefs", android.content.Context.MODE_PRIVATE)
        val savedLangCode = sharedPrefs.getString("ui_language_code", null)
        if (savedLangCode != null) {
            _uiLanguage.value = AppLanguage.fromCode(savedLangCode)
        } else {
            val deviceLocale = Locale.getDefault().language
            val autoDetected = AppLanguage.fromCode(deviceLocale)
            _uiLanguage.value = autoDetected
            sharedPrefs.edit().putString("ui_language_code", autoDetected.code).apply()
        }

        // Initialize Android Native Text To Speech
        try {
            tts = TextToSpeech(application, this)
        } catch (e: Exception) {
            Log.e("TranslationViewModel", "Failed to init TTS: ${e.message}")
        }
    }

    fun selectUiLanguage(lang: AppLanguage) {
        _uiLanguage.value = lang
        val sharedPrefs = getApplication<Application>().getSharedPreferences("smart_translate_prefs", android.content.Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("ui_language_code", lang.code).apply()
    }

    // --- DB reactive states ---
    val allTranslations: StateFlow<List<TranslationEntity>> = repository.allTranslations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val favoriteTranslations: StateFlow<List<TranslationEntity>> = repository.favoriteTranslations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // --- UI Local Screen States ---
    private val _currentTab = MutableStateFlow("home") // home, chat, voice, camera, conversation, history, settings
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    // --- Primary Input Parameters ---
    private val _sourceText = MutableStateFlow("")
    val sourceText: StateFlow<String> = _sourceText.asStateFlow()

    private val _sourceLang = MutableStateFlow("English")
    val sourceLang: StateFlow<String> = _sourceLang.asStateFlow()

    private val _targetLang = MutableStateFlow("Thai")
    val targetLang: StateFlow<String> = _targetLang.asStateFlow()

    private val _selectedTone = MutableStateFlow("Natural") // Natural, Friendly, Professional, Casual, Romantic, Funny
    val selectedTone: StateFlow<String> = _selectedTone.asStateFlow()

    private val _selectedMode = MutableStateFlow("AI Natural") // AI Natural vs Direct
    val selectedMode: StateFlow<String> = _selectedMode.asStateFlow()

    private val _grammarCorrection = MutableStateFlow(false)
    val grammarCorrection: StateFlow<Boolean> = _grammarCorrection.asStateFlow()

    // --- Translation response engine state ---
    private val _translationResult = MutableStateFlow("")
    val translationResult: StateFlow<String> = _translationResult.asStateFlow()

    private val _isTranslating = MutableStateFlow(false)
    val isTranslating: StateFlow<Boolean> = _isTranslating.asStateFlow()

    private val _linguisticExplanation = MutableStateFlow("")
    val linguisticExplanation: StateFlow<String> = _linguisticExplanation.asStateFlow()

    private val _isExplaining = MutableStateFlow(false)
    val isExplaining: StateFlow<Boolean> = _isExplaining.asStateFlow()

    // --- Voice translation simulation ---
    private val _voiceRecordingState = MutableStateFlow(false) // Recording STT
    val voiceRecordingState: StateFlow<Boolean> = _voiceRecordingState.asStateFlow()

    // --- Conversation Screen State ---
    private val _topPersonRecording = MutableStateFlow(false)
    val topPersonRecording: StateFlow<Boolean> = _topPersonRecording.asStateFlow()

    private val _bottomPersonRecording = MutableStateFlow(false)
    val bottomPersonRecording: StateFlow<Boolean> = _bottomPersonRecording.asStateFlow()

    private val _conversationHistory = MutableStateFlow<List<ConversationUtterance>>(emptyList())
    val conversationHistory: StateFlow<List<ConversationUtterance>> = _conversationHistory.asStateFlow()

    // --- Camera Translation Mock Frame State ---
    private val _cameraScanningResult = MutableStateFlow<CameraScanState>(CameraScanState.Idle)
    val cameraScanningResult: StateFlow<CameraScanState> = _cameraScanningResult.asStateFlow()

    // Settings
    private val _speechRateMultiplier = MutableStateFlow(1.0f)
    val speechRateMultiplier: StateFlow<Float> = _speechRateMultiplier.asStateFlow()

    private val _offlineCacheEnabled = MutableStateFlow(true)
    val offlineCacheEnabled: StateFlow<Boolean> = _offlineCacheEnabled.asStateFlow()

    private var translationJob: Job? = null

    fun selectTab(tab: String) {
        _currentTab.value = tab
    }

    fun setSourceText(text: String) {
        _sourceText.value = text
        // Reset explanation when input edits to make UX tidy
        _linguisticExplanation.value = ""
    }

    fun setSourceLang(lang: String) {
        _sourceLang.value = lang
    }

    fun setTargetLang(lang: String) {
        _targetLang.value = lang
    }

    fun setTone(tone: String) {
        _selectedTone.value = tone
    }

    fun setMode(mode: String) {
        _selectedMode.value = mode
    }

    fun toggleGrammar() {
        _grammarCorrection.value = !_grammarCorrection.value
    }

    fun swapLanguages() {
        val temp = _sourceLang.value
        _sourceLang.value = _targetLang.value
        _targetLang.value = temp
        
        val tempText = _sourceText.value
        _sourceText.value = _translationResult.value
        _translationResult.value = tempText
    }

    // --- Clear current input and outputs ---
    fun clearInput() {
        _sourceText.value = ""
        _translationResult.value = ""
        _linguisticExplanation.value = ""
    }

    // --- Run Translation with Gemini ---
    fun performAiTranslation() {
        val query = _sourceText.value.trim()
        if (query.isEmpty()) return

        translationJob?.cancel()
        _isTranslating.value = true
        _translationResult.value = ""
        _linguisticExplanation.value = ""

        translationJob = viewModelScope.launch {
            var fullOutput = ""
            GeminiApiClient.translateStream(
                text = query,
                sourceLang = _sourceLang.value,
                targetLang = _targetLang.value,
                mode = _selectedMode.value,
                tone = _selectedTone.value,
                grammarCorrection = _grammarCorrection.value
            ).collect { chunk ->
                // The stream may provide a growing sequence of completed string.
                // We show chunks appended to results.
                fullOutput += chunk
                _translationResult.value = fullOutput
            }
            _isTranslating.value = false

            // Automatically cache in history if offlineCache is enabled
            if (_offlineCacheEnabled.value && _translationResult.value.isNotEmpty() && !(_translationResult.value.startsWith("Error:"))) {
                repository.insertTranslation(
                    TranslationEntity(
                        sourceText = query,
                        targetText = _translationResult.value,
                        sourceLang = _sourceLang.value,
                        targetLang = _targetLang.value,
                        tone = _selectedTone.value,
                        mode = _selectedMode.value,
                        isFavorite = false
                    )
                )
            }
        }
    }

    // --- Generate deep Smart AI explanation ---
    fun getLinguisticExplanation() {
        val result = _translationResult.value
        if (result.isEmpty() || result.startsWith("Error:")) return

        _isExplaining.value = true
        _linguisticExplanation.value = ""

        viewModelScope.launch {
            var fullDesc = ""
            GeminiApiClient.explainPhrase(
                phrase = _sourceText.value,
                sourceLang = _sourceLang.value,
                targetLang = _targetLang.value
            ).collect { chunk ->
                fullDesc += chunk
                _linguisticExplanation.value = fullDesc
            }
            _isExplaining.value = false
        }
    }

    // --- Speaking output via Native TTS ---
    fun speakOutput(text: String, language: String) {
        if (!isTtsInitialized || tts == null) {
            Log.e("TranslationViewModel", "TTS not initialized!")
            return
        }
        val locale = getLocaleForLangName(language)
        tts?.language = locale
        tts?.setSpeechRate(_speechRateMultiplier.value)
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "TranslationID")
    }

    // --- Native Speech recognition simulation / Speech to Text Mock ---
    fun simulateVoiceSpeech(promptText: String) {
        _sourceText.value = promptText
        performAiTranslation()
    }

    fun startVoiceRecordingSim() {
        _voiceRecordingState.value = true
    }

    fun stopVoiceRecordingAndSimulate(recognizedText: String) {
        _voiceRecordingState.value = false
        _sourceText.value = recognizedText
        performAiTranslation()
    }

    // --- Conversation Screen split interactions ---
    fun toggleTopConversationRecording() {
        val currentlyRecording = _topPersonRecording.value
        if (!currentlyRecording) {
            _bottomPersonRecording.value = false
            _topPersonRecording.value = true
        } else {
            _topPersonRecording.value = false
            // Simulate that top speaker (English) said something, and we translate to Thai
            val utterances = listOf(
                "Hey, nice to meet you! Is there a good restaurant nearby?",
                "How much does this cost?",
                "I am traveling around Southeast Asia, it is so beautiful here."
            )
            val randomUtterance = utterances.random()
            addConversationPart(randomUtterance, sender = "top", fromLang = _sourceLang.value, toLang = _targetLang.value)
        }
    }

    fun toggleBottomConversationRecording() {
        val currentlyRecording = _bottomPersonRecording.value
        if (!currentlyRecording) {
            _topPersonRecording.value = false
            _bottomPersonRecording.value = true
        } else {
            _bottomPersonRecording.value = false
            // Simulate that bottom speaker (Thai) replied, and we translate to English
            val utterances = listOf(
                "สวัสดีครับ ยินดีที่ได้รู้จักครับ แถวนี้มีร้านอร่อยตั้งอยู่ตรงหัวมุมนะครับ",
                "อันนี้ราคาเจ็ดสิบบาทครับผม ลดราคาได้นิดหน่อยนะ",
                "ยินดีต้อนรับสู่เมืองไทยครับ ขอให้เดินทางท่องเที่ยวอย่างปลอดภัยนะ"
            )
            val randomUtterance = utterances.random()
            addConversationPart(randomUtterance, sender = "bottom", fromLang = _targetLang.value, toLang = _sourceLang.value)
        }
    }

    private fun addConversationPart(text: String, sender: String, fromLang: String, toLang: String) {
        viewModelScope.launch {
            val list = _conversationHistory.value.toMutableList()
            val newItem = ConversationUtterance(
                id = System.currentTimeMillis().toInt(),
                originalText = text,
                translatedText = "Translating...",
                sender = sender,
                timestamp = System.currentTimeMillis(),
                fromLang = fromLang,
                toLang = toLang
            )
            list.add(newItem)
            _conversationHistory.value = list

            // Query Gemini to get real natural translated result in real time!
            var fullText = ""
            GeminiApiClient.translateStream(
                text = text,
                sourceLang = fromLang,
                targetLang = toLang,
                mode = "AI Natural",
                tone = "Natural"
            ).collect { chunk ->
                fullText += chunk
                val currentList = _conversationHistory.value.toMutableList()
                val index = currentList.indexOfFirst { it.id == newItem.id }
                if (index != -1) {
                    currentList[index] = currentList[index].copy(translatedText = fullText)
                    _conversationHistory.value = currentList
                }
            }

            // Speak the computed native output automatically like live dynamic audio translator!
            val currentList = _conversationHistory.value
            val finalizedItem = currentList.firstOrNull { it.id == newItem.id }
            if (finalizedItem != null && finalizedItem.translatedText.isNotEmpty()) {
                speakOutput(finalizedItem.translatedText, toLang)
            }
        }
    }

    fun clearConversations() {
        _conversationHistory.value = emptyList()
    }

    // --- Camera Mock Screen Scan actions ---
    fun startCameraMockScan(simulatedImageUrl: String = "") {
        _cameraScanningResult.value = CameraScanState.Scanning
        viewModelScope.launch {
            kotlinx.coroutines.delay(2000) // Simulate OCR processing delay
            // Simulated scanned text from camera depending on the standard selected source language
            val scans = mapOf(
                "English" to "CAUTION: Wet Floor and slippery surface. Please watch your step.",
                "Thai" to "ยินดีต้อนรับสู่ประเทศไทย ดินแดนแห่งรอยยิ้มอันแสนอบอุ่น",
                "Japanese" to "本日の一部商品は30%割引となります。レジにてお知らせください。",
                "Korean" to "우리는 매일 맛있는 신선한 빵을 굽습니다. 행복한 하루 되세요!",
                "Spanish" to "¡Cuidado! Cruce de peatones. Reduzca la velocidad inmediatamente.",
                "Arabic" to "خطر! منطقة العمل في البناء. ممنوع الدخول لغير المصرح لهم."
            )
            val extractedText = scans[_sourceLang.value] ?: "WARNING: Please authorize system update and verify details."
            
            _cameraScanningResult.value = CameraScanState.Completed(extractedText)
            _sourceText.value = extractedText
            performAiTranslation()
        }
    }

    fun resetCameraScan() {
        _cameraScanningResult.value = CameraScanState.Idle
    }

    // --- Favorites DB operations ---
    fun toggleFavorite(entity: TranslationEntity) {
        viewModelScope.launch {
            repository.toggleFavorite(entity.id, !entity.isFavorite)
        }
    }

    fun deleteTranslation(id: Int) {
        viewModelScope.launch {
            repository.deleteTranslation(id)
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            repository.clearHistory()
        }
    }

    fun updateSpeechRate(rate: Float) {
        _speechRateMultiplier.value = rate
    }

    fun toggleOfflineCache(enabled: Boolean) {
        _offlineCacheEnabled.value = enabled
    }

    // Utility language converter
    private fun getLocaleForLangName(lang: String): Locale {
        return when (lang) {
            "English" -> Locale.US
            "Thai" -> Locale("th", "TH")
            "Hebrew" -> Locale("he", "IL")
            "Japanese" -> Locale.JAPAN
            "Korean" -> Locale.KOREA
            "Chinese" -> Locale.CHINA
            "Russian" -> Locale("ru", "RU")
            "Arabic" -> Locale("ar", "AE")
            "Spanish" -> Locale("es", "ES")
            else -> Locale.getDefault()
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isTtsInitialized = true
            tts?.language = Locale.US
        } else {
            Log.e("TranslationViewModel", "TTS initialization failed")
        }
    }

    override fun onCleared() {
        super.onCleared()
        tts?.shutdown()
    }
}

// Helper models
data class ConversationUtterance(
    val id: Int,
    val originalText: String,
    val translatedText: String,
    val sender: String, // top vs bottom
    val timestamp: Long,
    val fromLang: String,
    val toLang: String
)

sealed interface CameraScanState {
    object Idle : CameraScanState
    object Scanning : CameraScanState
    data class Completed(val textScanned: String) : CameraScanState
}

package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.Locale

class QuranSpellingPronunciationManager(private val context: Context) : TextToSpeech.OnInitListener {

    private val TAG = "SpellingPronounceMgr"
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val scope = CoroutineScope(Dispatchers.Main + Job())
    private var sequenceJob: Job? = null

    private val _currentlyPronouncingText = MutableStateFlow<String?>(null)
    val currentlyPronouncingText: StateFlow<String?> = _currentlyPronouncingText.asStateFlow()

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context.applicationContext, this)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to instantiate TextToSpeech", e)
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val ttsInstance = tts
            if (ttsInstance != null) {
                val arabicLocales = listOf(
                    Locale("ar", "SA"),
                    Locale("ar"),
                    Locale.forLanguageTag("ar-SA"),
                    Locale.forLanguageTag("ar")
                )
                var langSet = false
                for (locale in arabicLocales) {
                    val result = ttsInstance.setLanguage(locale)
                    if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                        langSet = true
                        Log.d(TAG, "TextToSpeech initialized with locale: $locale")
                        break
                    }
                }
                if (!langSet) {
                    ttsInstance.language = Locale.getDefault()
                    Log.w(TAG, "Arabic locale not fully supported, falling back to default locale")
                }
                ttsInstance.setSpeechRate(0.80f)
                ttsInstance.setPitch(1.0f)
                isInitialized = true

                ttsInstance.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                    override fun onStart(utteranceId: String?) {
                        _isSpeaking.value = true
                    }

                    override fun onDone(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentlyPronouncingText.value = null
                    }

                    @Deprecated("Deprecated in Java")
                    override fun onError(utteranceId: String?) {
                        _isSpeaking.value = false
                        _currentlyPronouncingText.value = null
                    }
                })
            }
        } else {
            Log.e(TAG, "TextToSpeech init failed with status: $status")
            isInitialized = false
        }
    }

    /**
     * نطق الحرف أو الكلمة الواحدة بصوت القارئ المعلم
     */
    fun pronounce(text: String) {
        val clean = text.trim()
        if (clean.isBlank()) return
        stop()

        _currentlyPronouncingText.value = clean
        _isSpeaking.value = true

        val ttsInstance = tts
        if (ttsInstance != null && isInitialized) {
            val params = android.os.Bundle()
            val utteranceId = "spelling_${System.currentTimeMillis()}"
            ttsInstance.speak(clean, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
        } else {
            // Simulated delay for devices where TTS is not configured or in unit tests
            scope.launch {
                delay(800)
                _isSpeaking.value = false
                _currentlyPronouncingText.value = null
            }
        }
    }

    /**
     * نطق خطوات التهجي كاملة بالترتيب (الحرف الأول، الحرف الثاني، المجموع، ثم الكلمة كاملة)
     */
    fun pronounceSequence(steps: List<String>, stepDelayMs: Long = 950L) {
        stop()
        if (steps.isEmpty()) return

        sequenceJob = scope.launch {
            for (step in steps) {
                if (!isActive) break
                val trimmed = step.trim()
                if (trimmed.isNotBlank()) {
                    _currentlyPronouncingText.value = trimmed
                    _isSpeaking.value = true
                    val ttsInstance = tts
                    if (ttsInstance != null && isInitialized) {
                        val params = android.os.Bundle()
                        val utteranceId = "seq_${System.currentTimeMillis()}"
                        ttsInstance.speak(trimmed, TextToSpeech.QUEUE_FLUSH, params, utteranceId)
                    }
                    delay(stepDelayMs)
                }
            }
            _isSpeaking.value = false
            _currentlyPronouncingText.value = null
        }
    }

    fun stop() {
        sequenceJob?.cancel()
        sequenceJob = null
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.w(TAG, "Error stopping TTS", e)
        }
        _isSpeaking.value = false
        _currentlyPronouncingText.value = null
    }

    fun release() {
        stop()
        try {
            tts?.shutdown()
        } catch (e: Exception) {
            Log.w(TAG, "Error shutting down TTS", e)
        }
        tts = null
        isInitialized = false
    }
}

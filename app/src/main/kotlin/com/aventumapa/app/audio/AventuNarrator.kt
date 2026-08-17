package com.aventumapa.app.audio

import android.content.Context
import android.media.AudioAttributes
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.speech.tts.Voice
import com.aventumapa.core.model.NarratorVoice
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

private data class SpeechRequest(
    val text: String,
    val persona: NarratorVoice,
    val utteranceId: String,
)

@Singleton
class AventuNarrator @Inject constructor(
    @ApplicationContext context: Context,
) : TextToSpeech.OnInitListener {
    private var engine: TextToSpeech? = null
    private var ready = false
    private var pendingSpeech: Pair<String, NarratorVoice>? = null
    private var activeRequest: SpeechRequest? = null
    private val mainHandler = Handler(Looper.getMainLooper())

    init {
        engine = TextToSpeech(context.applicationContext, this)
    }

    override fun onInit(status: Int) {
        val textToSpeech = engine ?: return
        ready = status == TextToSpeech.SUCCESS
        if (!ready) return
        textToSpeech.language = Locale.forLanguageTag("es-MX")
        textToSpeech.setAudioAttributes(
            AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_ASSISTANCE_ACCESSIBILITY)
                .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                .build(),
        )
        textToSpeech.setOnUtteranceProgressListener(
            object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) = Unit

                override fun onDone(utteranceId: String?) = Unit

                @Suppress("OVERRIDE_DEPRECATION")
                override fun onError(utteranceId: String?) {
                    retryWithOfflineVoice(utteranceId)
                }

                override fun onError(utteranceId: String?, errorCode: Int) {
                    retryWithOfflineVoice(utteranceId)
                }
            },
        )
        pendingSpeech?.let { (text, voice) -> speak(text, voice) }
        pendingSpeech = null
    }

    fun speak(text: String, voice: NarratorVoice) {
        val textToSpeech = engine
        if (!ready || textToSpeech == null) {
            pendingSpeech = text to voice
            return
        }
        val request = SpeechRequest(
            text = text.trim(),
            persona = voice,
            utteranceId = "aventumapa-${System.nanoTime()}",
        )
        activeRequest = request
        speakRequest(textToSpeech, request, allowNetworkVoice = true)
    }

    fun stop() {
        activeRequest = null
        engine?.stop()
    }

    private fun speakRequest(
        textToSpeech: TextToSpeech,
        request: SpeechRequest,
        allowNetworkVoice: Boolean,
    ) {
        configure(textToSpeech, request.persona, allowNetworkVoice)
        val params = Bundle().apply {
            putFloat(TextToSpeech.Engine.KEY_PARAM_VOLUME, 0.94f)
        }
        val suffix = if (allowNetworkVoice) "" else "-offline"
        textToSpeech.speak(
            request.text,
            TextToSpeech.QUEUE_FLUSH,
            params,
            request.utteranceId + suffix,
        )
    }

    private fun retryWithOfflineVoice(failedUtteranceId: String?) {
        val request = activeRequest ?: return
        if (failedUtteranceId != request.utteranceId) return
        mainHandler.post {
            engine?.let { speakRequest(it, request, allowNetworkVoice = false) }
        }
    }

    private fun configure(
        textToSpeech: TextToSpeech,
        persona: NarratorVoice,
        allowNetworkVoice: Boolean,
    ) {
        val candidates = textToSpeech.voices
            .orEmpty()
            .filter { it.locale.language == "es" }
            .filter { allowNetworkVoice || !it.isNetworkConnectionRequired }
            .sortedByDescending { voiceScore(it, allowNetworkVoice) }

        if (candidates.isNotEmpty()) {
            val highQualityPool = candidates.filter { it.quality >= Voice.QUALITY_HIGH }.ifEmpty { candidates }
            textToSpeech.voice = highQualityPool[persona.ordinal % highQualityPool.size]
        } else {
            textToSpeech.language = Locale.forLanguageTag("es-MX")
        }

        // Ajustes sutiles: evitan el efecto caricaturesco y reducen fatiga auditiva.
        val (pitch, rate) = when (persona) {
            NarratorVoice.BOY -> 1.08f to 0.95f
            NarratorVoice.GIRL -> 1.11f to 0.97f
            NarratorVoice.ELEGANT_MAN -> 0.94f to 0.91f
            NarratorVoice.FRIENDLY_WOMAN -> 1.02f to 0.94f
        }
        textToSpeech.setPitch(pitch)
        textToSpeech.setSpeechRate(rate)
    }

    private fun voiceScore(voice: Voice, allowNetworkVoice: Boolean): Int {
        val localeBonus = when (voice.locale.country.uppercase(Locale.ROOT)) {
            "MX" -> 500
            "US" -> 240
            else -> 100
        }
        val networkBonus = if (allowNetworkVoice && voice.isNetworkConnectionRequired) 160 else 0
        return voice.quality * 10 - voice.latency + localeBonus + networkBonus
    }
}

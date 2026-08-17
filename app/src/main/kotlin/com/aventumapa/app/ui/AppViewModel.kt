package com.aventumapa.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aventumapa.app.audio.AventuNarrator
import com.aventumapa.app.audio.AventuSoundEffects
import com.aventumapa.app.audio.SoundCue
import com.aventumapa.app.data.ProfilePreferencesRepository
import com.aventumapa.core.model.ChildProfile
import com.aventumapa.core.model.NarratorVoice
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppUiState(
    val isLoading: Boolean = true,
    val profile: ChildProfile = ChildProfile(),
)

@HiltViewModel
class AppViewModel @Inject constructor(
    private val profileRepository: ProfilePreferencesRepository,
    private val narrator: AventuNarrator,
    private val soundEffects: AventuSoundEffects,
) : ViewModel() {
    val uiState: StateFlow<AppUiState> = profileRepository.profile
        .map { AppUiState(isLoading = false, profile = it) }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AppUiState())

    fun saveProfile(alias: String, avatarId: String) {
        viewModelScope.launch {
            profileRepository.createOrUpdateProfile(alias, avatarId)
        }
    }

    fun recordRound(correct: Int, total: Int, stars: Int) {
        viewModelScope.launch {
            profileRepository.recordRound(correct, total, stars)
        }
    }

    fun selectNarratorVoice(voice: NarratorVoice) {
        viewModelScope.launch {
            profileRepository.updateNarratorVoice(voice)
        }
        narrator.speak(voicePreview(voice, uiState.value.profile.alias), voice)
    }

    fun speak(text: String) {
        narrator.speak(text, uiState.value.profile.narratorVoice)
    }

    fun stopSpeaking() {
        narrator.stop()
    }

    fun playSound(cue: SoundCue) {
        soundEffects.play(cue)
    }

    fun deleteProfile() {
        viewModelScope.launch {
            profileRepository.deleteLocalProfile()
        }
    }

    private fun voicePreview(voice: NarratorVoice, alias: String): String {
        val name = alias.ifBlank { "explorador" }
        return when (voice) {
            NarratorVoice.BOY -> "¡Hola, $name! Soy Matein Pompin. ¿Listo para explorar México?"
            NarratorVoice.GIRL -> "¡Hola, $name! Soy Andreita. Vamos a descubrir algo increíble."
            NarratorVoice.ELEGANT_MAN -> "Bienvenido, $name. Mi nombre es Maximo. Comencemos nuestra expedición."
            NarratorVoice.FRIENDLY_WOMAN -> "Hola, $name. Soy Claudis. Será un gusto acompañarte en esta aventura."
        }
    }
}

package com.aventumapa.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aventumapa.app.data.ProfilePreferencesRepository
import com.aventumapa.core.model.ChildProfile
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

    fun deleteProfile() {
        viewModelScope.launch {
            profileRepository.deleteLocalProfile()
        }
    }
}


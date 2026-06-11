package com.tutorconnect.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutorconnect.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val repository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _events = Channel<ProfileUiEvent>()
    val events = _events.receiveAsFlow()

    init { load() }

    fun onAction(action: ProfileUiAction) {
        when (action) {
            ProfileUiAction.Retry -> load()
            ProfileUiAction.EditProfile -> viewModelScope.launch {
                _events.send(ProfileUiEvent.ShowMessage("Edición de perfil próximamente."))
            }
            ProfileUiAction.Logout -> logout()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.currentUser() }
                .onSuccess { user -> _uiState.update { it.copy(user = user, isLoading = false) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            runCatching { repository.logout() }
            _uiState.update { it.copy(isLoggingOut = false) }
            _events.send(ProfileUiEvent.LoggedOut)
        }
    }
}

package com.tutorconnect.presentation.profile

import com.tutorconnect.data.model.UserProfile

data class ProfileUiState(
    val user: UserProfile? = null,
    val isLoading: Boolean = true,
    val isLoggingOut: Boolean = false,
    val error: String? = null,
)

sealed interface ProfileUiAction {
    data object Retry : ProfileUiAction
    data object EditProfile : ProfileUiAction
    data object Logout : ProfileUiAction
}

sealed interface ProfileUiEvent {
    data object LoggedOut : ProfileUiEvent
    data class ShowMessage(val message: String) : ProfileUiEvent
}

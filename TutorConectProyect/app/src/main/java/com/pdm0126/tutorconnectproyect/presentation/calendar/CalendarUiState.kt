package com.tutorconnect.presentation.calendar

import com.tutorconnect.data.model.TutoringSession

data class CalendarUiState(
    val sessions: List<TutoringSession> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

sealed interface CalendarUiAction {
    data object Retry : CalendarUiAction
    data class SessionClicked(val sessionId: String) : CalendarUiAction
}

sealed interface CalendarUiEvent {
    data class ShowMessage(val message: String) : CalendarUiEvent
}

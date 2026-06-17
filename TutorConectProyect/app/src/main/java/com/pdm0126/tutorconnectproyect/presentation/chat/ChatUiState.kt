package com.tutorconnect.presentation.chat

import com.tutorconnect.data.model.ChatMessage

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val draft: String = "",
    val isLoading: Boolean = true,
)

sealed interface ChatUiAction {
    data class Load(val tutorId: String) : ChatUiAction
    data class DraftChanged(val value: String) : ChatUiAction
    data object Send : ChatUiAction
    data object Back : ChatUiAction
}

sealed interface ChatUiEvent {
    data object Back : ChatUiEvent
    data object ScrollToBottom : ChatUiEvent
}

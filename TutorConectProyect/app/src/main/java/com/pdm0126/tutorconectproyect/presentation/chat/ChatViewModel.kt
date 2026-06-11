package com.tutorconnect.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutorconnect.data.repository.ChatRepository
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
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _events = Channel<ChatUiEvent>()
    val events = _events.receiveAsFlow()

    private var tutorId: String = ""
    private var loaded = false

    fun onAction(action: ChatUiAction) {
        when (action) {
            is ChatUiAction.Load -> load(action.tutorId)
            is ChatUiAction.DraftChanged -> _uiState.update { it.copy(draft = action.value) }
            ChatUiAction.Send -> send()
            ChatUiAction.Back -> viewModelScope.launch { _events.send(ChatUiEvent.Back) }
        }
    }

    private fun load(id: String) {
        if (loaded) return
        loaded = true
        tutorId = id
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val messages = repository.messages(id)
            _uiState.update { it.copy(messages = messages.distinctBy { m -> m.id }, isLoading = false) }
            _events.send(ChatUiEvent.ScrollToBottom)
        }
    }

    private fun send() {
        val text = _uiState.value.draft.trim()
        if (text.isEmpty()) return
        viewModelScope.launch {
            val message = repository.send(tutorId, text)
            _uiState.update { 
                it.copy(
                    messages = (it.messages + message).distinctBy { m -> m.id },
                    draft = ""
                ) 
            }
            _events.send(ChatUiEvent.ScrollToBottom)
        }
    }
}

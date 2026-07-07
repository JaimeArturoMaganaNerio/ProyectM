package com.pdm0126.tutorconnectproyect.presentation.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm0126.tutorconnectproyect.data.model.ChatMessage
import com.pdm0126.tutorconnectproyect.data.repository.AuthRepository
import com.pdm0126.tutorconnectproyect.data.repository.ChatRepository
import com.pdm0126.tutorconnectproyect.domain.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val _events = Channel<ChatUiEvent>()
    val events = _events.receiveAsFlow()

    private var currentUserId: String = ""
    private var receiverUserId: String = ""

    fun onAction(action: ChatUiAction) {
        when (action) {
            is ChatUiAction.Load -> {
                receiverUserId = action.tutorId
                viewModelScope.launch {
                        val user = authRepository.currentUser.filterNotNull().first()
                    currentUserId = user.id
                    listenForMessages()
                }
            }
            is ChatUiAction.DraftChanged -> _uiState.update { it.copy(draft = action.value) }
            ChatUiAction.Send -> send()
            ChatUiAction.Back -> viewModelScope.launch { _events.send(ChatUiEvent.Back) }
        }
    }

    private fun listenForMessages() {
        viewModelScope.launch {
            chatRepository.getMessages(currentUserId, receiverUserId).collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // fromMe no se guarda en Firestore, lo calculamos al leer
                        val mapped = result.data.map { it.copy(fromMe = it.senderId == currentUserId) }
                        _uiState.update { it.copy(messages = mapped, isLoading = false) }
                        if (mapped.isNotEmpty()) _events.send(ChatUiEvent.ScrollToBottom)
                    }
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false) }
                    else -> {}
                }
            }
        }
    }

    private fun send() {
        val text = _uiState.value.draft.trim()
        if (text.isEmpty() || currentUserId.isEmpty()) return

        viewModelScope.launch {
            val newMessage = ChatMessage(
                senderId = currentUserId,
                receiverId = receiverUserId,
                message = text,
                timestamp = Date() // Solucionado el error de fecha
            )
            _uiState.update { it.copy(draft = "") }
            chatRepository.sendMessage(newMessage)
        }
    }
}

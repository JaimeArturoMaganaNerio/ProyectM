package com.pdm0126.tutorconnectproyect.presentation.messages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm0126.tutorconnectproyect.data.model.Conversation
import com.pdm0126.tutorconnectproyect.data.repository.AuthRepository
import com.pdm0126.tutorconnectproyect.data.repository.ChatRepository
import com.pdm0126.tutorconnectproyect.data.repository.TutorRepository
import com.pdm0126.tutorconnectproyect.domain.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MessagesUiState(
    val conversations: List<Conversation> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

@HiltViewModel
class MessagesViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository,
    private val tutorRepository: TutorRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MessagesUiState())
    val uiState: StateFlow<MessagesUiState> = _uiState.asStateFlow()

    init {
        loadConversations()
    }

    private fun loadConversations() {
        viewModelScope.launch {
            // filterNotNull().first() espera hasta recibir un usuario real (no null)
            val user = try {
                authRepository.currentUser
                    .filterNotNull()
                    .first()
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, error = "Sesión no disponible") }
                return@launch
            }

            chatRepository.getConversations(user.id).collect { result ->
                when (result) {
                    is Resource.Loading -> _uiState.update { it.copy(isLoading = true, error = null) }
                    is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                    is Resource.Success -> {
                        val withNames = result.data.map { conv ->
                            // getTutorById busca en la colección "users" por ID,
                            // funciona para cualquier usuario (tutor o tutorado)
                            val nameResult = tutorRepository.getTutorById(conv.otherUserId)
                            val resolvedName = when {
                                nameResult is Resource.Success && nameResult.data.name.isNotBlank() ->
                                    nameResult.data.name
                                else -> "Usuario"
                            }
                            conv.copy(otherUserName = resolvedName)
                        }
                        _uiState.update { it.copy(isLoading = false, conversations = withNames) }
                    }
                }
            }
        }
    }
}

package com.pdm0126.tutorconectproyect.presentation.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm0126.tutorconectproyect.data.model.Post
import com.pdm0126.tutorconectproyect.data.repository.AuthRepository
import com.pdm0126.tutorconectproyect.data.repository.PostRepository
import com.tutorconnect.domain.Resource
import com.tutorconnect.presentation.post.CreatePostUiAction
import com.tutorconnect.presentation.post.CreatePostUiEvent
import com.tutorconnect.presentation.post.CreatePostUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

@HiltViewModel
class CreatePostViewModel @Inject constructor(
    private val repository: PostRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    private val _events = Channel<CreatePostUiEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: CreatePostUiAction) {
        when (action) {
            is CreatePostUiAction.TitleChanged -> _uiState.update { it.copy(title = action.value) }
            is CreatePostUiAction.DescriptionChanged -> _uiState.update { it.copy(description = action.value) }
            CreatePostUiAction.AttachFile -> _uiState.update { it.copy(attachmentName = "documento.pdf") }
            CreatePostUiAction.RemoveAttachment -> _uiState.update { it.copy(attachmentName = null) }
            CreatePostUiAction.Submit -> submit()
        }
    }

    private fun submit() {
        val current = _uiState.value
        if (!current.isValid) {
            _uiState.update { it.copy(showErrors = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val user = authRepository.currentUser.firstOrNull()

            val post = Post(
                authorId = user?.id ?: "",
                authorName = user?.name ?: "Usuario",
                title = current.title.trim(),
                content = current.description.trim(), // Tu UI le llama description, Firebase le llama content
                timestamp = Date()
            )

            when (val result = repository.createPost(post)) {
                is Resource.Success -> {
                    _uiState.update { CreatePostUiState() }
                    _events.send(CreatePostUiEvent.Published)
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.send(CreatePostUiEvent.ShowMessage(result.message))
                }
                is Resource.Loading -> {}
            }
        }
    }
}
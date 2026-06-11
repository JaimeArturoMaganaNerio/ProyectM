package com.tutorconnect.presentation.post

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutorconnect.data.model.NewPost
import com.tutorconnect.data.repository.PostRepository
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
class CreatePostViewModel @Inject constructor(
    private val repository: PostRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePostUiState())
    val uiState: StateFlow<CreatePostUiState> = _uiState.asStateFlow()

    private val _events = Channel<CreatePostUiEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: CreatePostUiAction) {
        when (action) {
            is CreatePostUiAction.TitleChanged -> _uiState.update { it.copy(title = action.value) }
            is CreatePostUiAction.DescriptionChanged -> _uiState.update { it.copy(description = action.value) }
            CreatePostUiAction.AttachFile -> _uiState.update {
                it.copy(attachmentName = "documento_adjunto.pdf")
            }
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
            val post = NewPost(
                title = current.title.trim(),
                description = current.description.trim(),
                attachmentName = current.attachmentName,
            )
            repository.publish(post)
                .onSuccess {
                    _uiState.update { CreatePostUiState() }
                    _events.send(CreatePostUiEvent.ShowMessage("¡Publicación creada!"))
                    _events.send(CreatePostUiEvent.Published)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.send(CreatePostUiEvent.ShowMessage(e.message ?: "No se pudo publicar."))
                }
        }
    }
}

package com.tutorconnect.presentation.tutor_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutorconnect.data.repository.TutorRepository
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
class TutorDetailViewModel @Inject constructor(
    private val repository: TutorRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TutorDetailUiState())
    val uiState: StateFlow<TutorDetailUiState> = _uiState.asStateFlow()

    private val _events = Channel<TutorDetailUiEvent>()
    val events = _events.receiveAsFlow()

    private var loadedId: String? = null

    fun onAction(action: TutorDetailUiAction) {
        when (action) {
            is TutorDetailUiAction.Load -> load(action.tutorId)
            TutorDetailUiAction.SendNudge -> emit(TutorDetailUiEvent.ShowMessage("¡Nudge enviado al tutor!"))
            TutorDetailUiAction.OpenChat -> _uiState.value.tutor?.let {
                emit(TutorDetailUiEvent.OpenChat(it.id, it.name))
            }
            TutorDetailUiAction.Book -> _uiState.value.tutor?.let {
                emit(TutorDetailUiEvent.Book(it.id, it.name))
            }
            TutorDetailUiAction.Back -> emit(TutorDetailUiEvent.Back)
        }
    }

    private fun emit(event: TutorDetailUiEvent) = viewModelScope.launch { _events.send(event) }

    private fun load(tutorId: String) {
        if (loadedId == tutorId) return
        loadedId = tutorId
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.tutorById(tutorId) }
                .onSuccess { tutor ->
                    if (tutor != null) _uiState.update { it.copy(tutor = tutor, isLoading = false) }
                    else _uiState.update { it.copy(isLoading = false, error = "Tutor no encontrado.") }
                }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}

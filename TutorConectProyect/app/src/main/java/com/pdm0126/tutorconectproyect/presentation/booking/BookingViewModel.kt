package com.tutorconnect.presentation.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutorconnect.data.model.BookingRequest
import com.tutorconnect.data.repository.BookingRepository
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
class BookingViewModel @Inject constructor(
    private val repository: BookingRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState: StateFlow<BookingUiState> = _uiState.asStateFlow()

    private val _events = Channel<BookingUiEvent>()
    val events = _events.receiveAsFlow()

    private var tutorId: String = ""
    private var initialized = false

    fun onAction(action: BookingUiAction) {
        when (action) {
            is BookingUiAction.Init -> init(action.tutorId, action.tutorName)
            is BookingUiAction.SubjectChanged -> _uiState.update { it.copy(subject = action.value) }
            is BookingUiAction.DateChanged -> _uiState.update { it.copy(date = action.value) }
            is BookingUiAction.TimeChanged -> _uiState.update { it.copy(time = action.value) }
            is BookingUiAction.CommentsChanged -> _uiState.update { it.copy(comments = action.value) }
            BookingUiAction.Submit -> submit()
            BookingUiAction.Back -> emit(BookingUiEvent.Back)
        }
    }

    private fun init(id: String, name: String) {
        if (initialized) return
        initialized = true
        tutorId = id
        _uiState.update { it.copy(tutorName = name) }
    }

    private fun submit() {
        val current = _uiState.value
        if (!current.isValid) {
            _uiState.update { it.copy(showErrors = true) }
            return
        }
        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }
            val request = BookingRequest(
                tutorId = tutorId,
                subject = current.subject.trim(),
                date = current.date.trim(),
                time = current.time.trim(),
                comments = current.comments.trim(),
            )
            repository.book(request)
                .onSuccess {
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.send(BookingUiEvent.ShowMessage("¡Tutoría reservada con éxito!"))
                    _events.send(BookingUiEvent.Booked)
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.send(BookingUiEvent.ShowMessage(e.message ?: "No se pudo reservar."))
                }
        }
    }

    private fun emit(event: BookingUiEvent) = viewModelScope.launch { _events.send(event) }
}

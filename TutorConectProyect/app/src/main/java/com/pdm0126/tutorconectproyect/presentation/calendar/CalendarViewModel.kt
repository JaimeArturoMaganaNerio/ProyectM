package com.tutorconnect.presentation.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutorconnect.data.repository.CalendarRepository
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
class CalendarViewModel @Inject constructor(
    private val repository: CalendarRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CalendarUiState())
    val uiState: StateFlow<CalendarUiState> = _uiState.asStateFlow()

    private val _events = Channel<CalendarUiEvent>()
    val events = _events.receiveAsFlow()

    init { load() }

    fun onAction(action: CalendarUiAction) {
        when (action) {
            CalendarUiAction.Retry -> load()
            is CalendarUiAction.SessionClicked -> viewModelScope.launch {
                _events.send(CalendarUiEvent.ShowMessage("Detalle de la sesión ${action.sessionId}"))
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching { repository.sessions() }
                .onSuccess { sessions -> _uiState.update { it.copy(sessions = sessions.distinctBy { it.id }, isLoading = false) } }
                .onFailure { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
        }
    }
}

package com.tutorconnect.presentation.tutors

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
class TutorsViewModel @Inject constructor(
    private val repository: TutorRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(TutorsUiState())
    val uiState: StateFlow<TutorsUiState> = _uiState.asStateFlow()

    private val _events = Channel<TutorsUiEvent>()
    val events = _events.receiveAsFlow()

    init { load() }

    fun onAction(action: TutorsUiAction) {
        when (action) {
            is TutorsUiAction.QueryChanged -> _uiState.update { it.copy(query = action.value) }
            is TutorsUiAction.FacultySelected -> _uiState.update { it.copy(selectedFaculty = action.faculty) }
            is TutorsUiAction.SubjectSelected -> _uiState.update { it.copy(selectedSubject = action.subject) }
            is TutorsUiAction.TutorClicked -> viewModelScope.launch {
                _events.send(TutorsUiEvent.OpenTutorDetail(action.tutorId))
            }
            TutorsUiAction.Retry -> load()
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                Triple(repository.tutors(), repository.faculties(), repository.subjects())
            }.onSuccess { (tutors, faculties, subjects) ->
                _uiState.update {
                    it.copy(
                        tutors = tutors,
                        faculties = faculties,
                        subjects = subjects,
                        isLoading = false,
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al cargar tutores.") }
            }
        }
    }
}

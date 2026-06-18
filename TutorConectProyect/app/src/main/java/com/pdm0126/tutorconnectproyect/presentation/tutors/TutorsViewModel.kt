package com.pdm0126.tutorconnectproyect.presentation.tutors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm0126.tutorconnectproyect.data.repository.TutorRepository
import com.pdm0126.tutorconnectproyect.domain.Resource
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
    private val tutorRepository: TutorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TutorsUiState())
    val uiState: StateFlow<TutorsUiState> = _uiState.asStateFlow()
    private val _events = Channel<TutorsUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        fetchTutors()
    }

    fun fetchTutors() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            tutorRepository.getAllTutors().collect { result ->
                when (result) {
                    is Resource.Success -> {
                        // Convertimos el User de Firebase al Tutor visual
                        val mappedTutors = result.data.map { user ->
                            com.pdm0126.tutorconnectproyect.data.model.Tutor(
                                id = user.id,
                                name = user.name,
                                subjects = user.subjects.ifEmpty { listOf("General") },
                                rating = user.rating,
                                photoUrl = user.profileImageUrl
                            )
                        }
                        // Actualizamos la UI con los datos nuevos
                        _uiState.update { it.copy(isLoading = false, tutors = mappedTutors) }
                    }
                    is Resource.Error -> {
                        // Si falla Firebase, lo mostramos
                        _uiState.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true, error = null) }
                    }
                }
            }
        }
    }

    fun onAction(action: TutorsUiAction) {
        when (action) {
            is TutorsUiAction.Retry -> fetchTutors()
            is TutorsUiAction.QueryChanged -> _uiState.update { it.copy(query = action.value) }
            is TutorsUiAction.FacultySelected -> _uiState.update { it.copy(selectedFaculty = action.faculty) }
            is TutorsUiAction.SubjectSelected -> _uiState.update { it.copy(selectedSubject = action.subject) }
            is TutorsUiAction.TutorClicked -> viewModelScope.launch {
                _events.send(TutorsUiEvent.OpenTutorDetail(action.tutorId))
            }
        }
    }
}
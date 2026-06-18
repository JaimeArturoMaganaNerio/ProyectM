package com.pdm0126.tutorconnectproyect.presentation.tutor_detail

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
class TutorDetailViewModel @Inject constructor(
    private val tutorRepository: TutorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TutorDetailUiState())
    val uiState: StateFlow<TutorDetailUiState> = _uiState.asStateFlow()

    private val _events = Channel<TutorDetailUiEvent>()
    val events = _events.receiveAsFlow()

    fun onAction(action: TutorDetailUiAction) {
        when (action) {
            is TutorDetailUiAction.Load -> loadTutor(action.tutorId)
            is TutorDetailUiAction.SendNudge -> viewModelScope.launch {
                _events.send(TutorDetailUiEvent.ShowMessage("Nudge enviado"))
            }

            is TutorDetailUiAction.Book -> viewModelScope.launch {
                val tutor = _uiState.value.tutor
                if (tutor != null) {
                    _events.send(TutorDetailUiEvent.Book(tutor.id, tutor.name))
                }
            }

            is TutorDetailUiAction.OpenChat -> viewModelScope.launch {
                val tutor = _uiState.value.tutor
                if (tutor != null) {
                    _events.send(TutorDetailUiEvent.OpenChat(tutor.id, tutor.name))
                }
            }

            is TutorDetailUiAction.Back -> viewModelScope.launch {
                _events.send(TutorDetailUiEvent.Back)
            }
        }
    }

    private fun loadTutor(tutorId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            // Llamada real a tu repositorio de Firebase
            when (val result = tutorRepository.getTutorById(tutorId)) {
                is Resource.Success -> {
                    val firebaseUser = result.data

                    // Mapeamos el modelo User de Firestore al modelo Tutor que usa tu interfaz
                    val tutorData = com.pdm0126.tutorconnectproyect.data.model.Tutor(
                        id = firebaseUser.id,
                        name = firebaseUser.name,
                        bio = firebaseUser.bio,
                        rating = firebaseUser.rating,
                        photoUrl = firebaseUser.profileImageUrl,
                        subjects = firebaseUser.subjects,
                        // Campos opcionales por si no existen en tu modelo User genérico de Firestore
                        specialty = firebaseUser.subjects.firstOrNull() ?: "Tutor Académico",
                        faculty = "Facultad de Ingeniería y Arquitectura",
                        status = com.pdm0126.tutorconnectproyect.data.model.TutorStatus.AVAILABLE,
                        schedule = listOf("Lunes a Viernes - Horario a convenir")
                    )

                    _uiState.update {
                        it.copy(isLoading = false, tutor = tutorData, error = null)
                    }
                }
                is Resource.Error -> {
                    _uiState.update {
                        it.copy(isLoading = false, error = result.message)
                    }
                }
                is Resource.Loading -> {
                    _uiState.update { it.copy(isLoading = true) }
                }
            }
        }
    }
}
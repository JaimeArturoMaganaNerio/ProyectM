package com.pdm0126.tutorconectproyect.presentation.tutor_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm0126.tutorconectproyect.data.repository.TutorRepository
import com.tutorconnect.domain.Resource
import com.tutorconnect.presentation.tutor_detail.TutorDetailUiAction
import com.tutorconnect.presentation.tutor_detail.TutorDetailUiEvent
import com.tutorconnect.presentation.tutor_detail.TutorDetailUiState
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
            is TutorDetailUiAction.LoadTutor -> loadTutor(action.tutorId)
            TutorDetailUiAction.BookSession -> viewModelScope.launch { _events.send(TutorDetailUiEvent.NavigateToBooking) }
            TutorDetailUiAction.StartChat -> viewModelScope.launch { _events.send(TutorDetailUiEvent.NavigateToChat) }
            TutorDetailUiAction.Back -> viewModelScope.launch { _events.send(TutorDetailUiEvent.NavigateBack) }
        }
    }

    private fun loadTutor(tutorId: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            when (val result = tutorRepository.getTutorById(tutorId)) {
                is Resource.Success -> {
                    val user = result.data
                    if (user != null) {
                        val mappedTutor = com.pdm0126.tutorconectproyect.data.model.Tutor(
                            id = user.id,
                            name = user.name,
                            subject = user.subjects.firstOrNull() ?: "General",
                            rating = user.rating,
                            imageUrl = user.profileImageUrl
                        )
                        _uiState.update { it.copy(isLoading = false, tutor = mappedTutor) }
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }
}
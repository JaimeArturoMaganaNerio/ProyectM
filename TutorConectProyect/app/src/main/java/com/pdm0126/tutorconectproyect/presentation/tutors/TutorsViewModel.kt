package com.pdm0126.tutorconectproyect.presentation.tutors

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm0126.tutorconectproyect.data.repository.TutorRepository
import com.tutorconnect.domain.Resource
import com.tutorconnect.presentation.tutors.TutorsUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TutorsViewModel @Inject constructor(
    private val tutorRepository: TutorRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(TutorsUiState())
    val uiState: StateFlow<TutorsUiState> = _uiState.asStateFlow()

    init {
        fetchTutors()
    }

    fun fetchTutors() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = tutorRepository.getAllTutors()) {
                is Resource.Success -> {
                    // Convertimos el User de Firebase al Tutor visual
                    val mappedTutors = result.data?.map { user ->
                        com.pdm0126.tutorconectproyect.data.model.Tutor(
                            id = user.id,
                            name = user.name,
                            subject = user.subjects.firstOrNull() ?: "General",
                            rating = user.rating,
                            imageUrl = user.profileImageUrl
                        )
                    } ?: emptyList()
                    _uiState.update { it.copy(isLoading = false, tutors = mappedTutors) }
                }
                is Resource.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                is Resource.Loading -> {}
            }
        }
    }
}
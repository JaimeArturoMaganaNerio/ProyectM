package com.tutorconnect.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tutorconnect.data.repository.DashboardRepository
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
class DashboardViewModel @Inject constructor(
    private val repository: DashboardRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _events = Channel<DashboardUiEvent>()
    val events = _events.receiveAsFlow()

    init { load() }

    fun onAction(action: DashboardUiAction) {
        when (action) {
            DashboardUiAction.Retry -> load()
            DashboardUiAction.OpenTutors -> viewModelScope.launch {
                _events.send(DashboardUiEvent.NavigateToTutors)
            }
            is DashboardUiAction.ReplyToPost -> viewModelScope.launch {
                _events.send(DashboardUiEvent.ShowMessage("Abriendo respuesta…"))
            }
        }
    }

    private fun load() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            runCatching {
                Triple(
                    repository.tutorSubjects(),
                    repository.additionalLoad(),
                    repository.featuredPosts(),
                )
            }.onSuccess { (subjects, extra, posts) ->
                _uiState.update {
                    it.copy(
                        tutorSubjects = subjects.distinctBy { it.id },
                        additionalLoad = extra.distinctBy { it.id },
                        featuredPosts = posts.distinctBy { it.id },
                        isLoading = false,
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, error = e.message ?: "Error al cargar.") }
            }
        }
    }
}

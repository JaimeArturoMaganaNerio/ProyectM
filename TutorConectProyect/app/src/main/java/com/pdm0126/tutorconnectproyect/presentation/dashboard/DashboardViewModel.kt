package com.pdm0126.tutorconnectproyect.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm0126.tutorconnectproyect.data.repository.PostRepository
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
class DashboardViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    private val _events = kotlinx.coroutines.channels.Channel<DashboardUiEvent>()
    val events = _events.receiveAsFlow()

    init {
        loadPosts()
    }

    fun onAction(action: DashboardUiAction) {
        when (action) {
            DashboardUiAction.Retry -> loadPosts()
            is DashboardUiAction.ReplyToPost -> {}
            DashboardUiAction.OpenTutors -> viewModelScope.launch { _events.send(DashboardUiEvent.NavigateToTutors) }
        }
    }

    fun loadPosts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = postRepository.getAllPosts()) {
                is Resource.Success -> {
                    val mapeado = result.data.map {
                        com.pdm0126.tutorconnectproyect.data.model.FeaturedPost(
                            id = it.id, authorName = it.authorName, question = it.title
                        )
                    }
                    _uiState.update { it.copy(isLoading = false, featuredPosts = mapeado) }
                }

                is Resource.Error -> _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = result.message
                    )
                }

                is Resource.Loading -> {}
            }
        }
    }
}

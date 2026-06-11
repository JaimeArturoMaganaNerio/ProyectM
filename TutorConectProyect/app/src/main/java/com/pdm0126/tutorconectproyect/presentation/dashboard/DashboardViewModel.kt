package com.pdm0126.tutorconectproyect.presentation.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.pdm0126.tutorconectproyect.data.repository.PostRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.tutorconnect.domain.Resource
import com.tutorconnect.presentation.dashboard.DashboardUiState

import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val postRepository: PostRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            when (val result = postRepository.getAllPosts()) {
                is Resource.Success -> {
                    val mapeado = result.data?.map {
                        com.pdm0126.tutorconectproyect.data.model.FeaturedPost(
                            id = it.id, authorName = it.authorName, question = it.title
                        )
                    } ?: emptyList()
                    // Si tu estado usa otro nombre, cambia 'featuredPosts' por el tuyo
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
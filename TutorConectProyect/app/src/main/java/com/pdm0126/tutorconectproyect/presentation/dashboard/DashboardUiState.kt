package com.tutorconnect.presentation.dashboard

import com.tutorconnect.data.model.FeaturedPost
import com.tutorconnect.data.model.Subject

data class DashboardUiState(
    val studentName: String = "",          // vendrá del backend al hacer login
    val tutorSubjects: List<Subject> = emptyList(),
    val additionalLoad: List<Subject> = emptyList(),
    val featuredPosts: List<FeaturedPost> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

sealed interface DashboardUiAction {
    data object Retry : DashboardUiAction
    data class ReplyToPost(val postId: String) : DashboardUiAction
    data object OpenTutors : DashboardUiAction
}

sealed interface DashboardUiEvent {
    data object NavigateToTutors : DashboardUiEvent
    data class ShowMessage(val message: String) : DashboardUiEvent
}

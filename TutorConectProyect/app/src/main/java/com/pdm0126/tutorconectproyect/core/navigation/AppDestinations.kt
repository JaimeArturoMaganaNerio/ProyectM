package com.pdm0126.tutorconectproyect.core.navigation

import androidx.navigation3.NavKey
import kotlinx.serialization.Serializable

sealed interface AppRoute : NavKey {

    @Serializable
    data object Login : AppRoute

    @Serializable
    data object Dashboard : AppRoute

    @Serializable
    data object Tutors : AppRoute

    @Serializable
    data class TutorDetail(val tutorId: String) : AppRoute

    @Serializable
    data class Chat(val receiverId: String) : AppRoute

    @Serializable
    data object CreatePost : AppRoute
}
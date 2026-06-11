package com.tutorconnect.data.model

/** Rol del usuario — se guarda al registrarse y define qué ve en la app */
enum class UserRole { TUTOR, TUTORADO }

/** Disponibilidad del tutor en el directorio */
enum class TutorStatus(val label: String) {
    ONLINE("En línea"),
    BUSY("Ocupado"),
    AVAILABLE("Disponible"),
}

data class UserProfile(
    val id: String,
    val fullName: String,
    val institutionalEmail: String,
    val career: String,
    val role: UserRole = UserRole.TUTORADO,
    val photoUrl: String? = null,
)

data class Subject(
    val id: String,
    val name: String,
    val faculty: String,
    val completed: Boolean = false,
)

data class Tutor(
    val id: String,
    val name: String,
    val specialty: String,
    val faculty: String,
    val status: TutorStatus,
    val photoUrl: String? = null,
    val subjects: List<String> = emptyList(),
    val schedule: List<String> = emptyList(),
    val bio: String = "",
    val rating: Double = 0.0,
)

data class FeaturedPost(
    val id: String,
    val authorName: String,
    val handle: String,
    val question: String,
    val photoUrl: String? = null,
)

data class ChatMessage(
    val id: String,
    val text: String,
    val fromMe: Boolean,
    val timestamp: String,
)

data class TutoringSession(
    val id: String,
    val tutorName: String,
    val subject: String,
    val date: String,
    val time: String,
    val confirmed: Boolean = false,
)

data class BookingRequest(
    val tutorId: String,
    val subject: String,
    val date: String,
    val time: String,
    val comments: String,
)

data class NewPost(
    val title: String,
    val description: String,
    val attachmentName: String? = null,
)

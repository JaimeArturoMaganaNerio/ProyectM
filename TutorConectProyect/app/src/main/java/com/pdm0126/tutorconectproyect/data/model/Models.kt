package com.pdm0126.tutorconectproyect.data.model

import com.google.firebase.firestore.DocumentId
import java.util.Date

// Agrega esto en data/model/Models.kt
enum class UserRole { TUTOR, TUTORADO }

enum class TutorStatus(val label: String) {
    ONLINE("En línea"),
    BUSY("Ocupado"),
    AVAILABLE("Disponible"),
}

// Necesitamos este modelo visual para el Dashboard
data class FeaturedPost(
    val id: String = "",
    val authorName: String = "",
    val handle: String = "",
    val question: String = "",
    val photoUrl: String? = null,
)


data class Tutor(
    val id: String = "",
    val name: String = "",
    val subject: String = "",
    val rating: Double = 0.0,
    val price: String = "Gratis",
    val status: TutorStatus = TutorStatus.ONLINE,
    val imageUrl: String? = null
)

data class UserProfile(
    val id: String = "",
    val fullName: String = "",
    val institutionalEmail: String = "",
    val career: String = "",
    val role: UserRole = UserRole.TUTORADO
)
// Usuario genérico (Puede ser Tutor o Estudiante)
data class User(
    @DocumentId val id: String = "",
    val uid: String = "", // Firebase Auth UID
    val name: String = "",
    val email: String = "",
    val role: String = "STUDENT", // "STUDENT" o "TUTOR"
    val profileImageUrl: String = "",
    val bio: String = "",
    val subjects: List<String> = emptyList(), // Materias que imparte si es tutor
    val rating: Double = 0.0
)

data class Post(
    @DocumentId val id: String = "",
    val authorId: String = "",
    val authorName: String = "",
    val title: String = "",
    val content: String = "",
    val fileUrl: String = "", // URL del PDF/Imagen en Firebase Storage
    val timestamp: Date = Date(),
    val tags: List<String> = emptyList()
)

data class Booking(
    @DocumentId val id: String = "",
    val studentId: String = "",
    val tutorId: String = "",
    val subject: String = "",
    val date: Date = Date(),
    val status: String = "PENDING", // PENDING, ACCEPTED, REJECTED, COMPLETED
    val notes: String = ""
)

data class ChatMessage(
    @DocumentId val id: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val message: String = "",
    val timestamp: Date = Date()
)

data class UiChatMessage(
    val id: String = "",
    val text: String = "",
    val fromMe: Boolean = false,
    val timestamp: String = ""
)


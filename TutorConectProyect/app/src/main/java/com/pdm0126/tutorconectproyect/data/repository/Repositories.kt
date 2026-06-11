package com.tutorconnect.data.repository

import com.tutorconnect.data.model.BookingRequest
import com.tutorconnect.data.model.ChatMessage
import com.tutorconnect.data.model.FeaturedPost
import com.tutorconnect.data.model.NewPost
import com.tutorconnect.data.model.Subject
import com.tutorconnect.data.model.Tutor
import com.tutorconnect.data.model.TutoringSession
import com.tutorconnect.data.model.UserProfile

/* ─────────────────────────────────────────────────────────────────────────────
 *  CONTRATOS DE REPOSITORIO
 *  Solo interfaces. Las implementaciones reales van contra MongoDB/backend.
 *  Para conectar: implementa cada interfaz y regístrala en di/RepositoryModule.kt
 * ──────────────────────────────────────────────────────────────────────────── */

interface AuthRepository {
    suspend fun login(email: String, password: String): Result<UserProfile>
    suspend fun loginWithMicrosoft(): Result<UserProfile>
}

interface DashboardRepository {
    suspend fun tutorSubjects(): List<Subject>
    suspend fun additionalLoad(): List<Subject>
    suspend fun featuredPosts(): List<FeaturedPost>
}

interface TutorRepository {
    suspend fun tutors(): List<Tutor>
    suspend fun tutorById(id: String): Tutor?
    suspend fun faculties(): List<String>
    suspend fun subjects(): List<String>
}

interface ChatRepository {
    suspend fun messages(tutorId: String): List<ChatMessage>
    suspend fun send(tutorId: String, text: String): ChatMessage
}

interface CalendarRepository {
    suspend fun sessions(): List<TutoringSession>
}

interface BookingRepository {
    suspend fun book(request: BookingRequest): Result<TutoringSession>
}

interface PostRepository {
    suspend fun publish(post: NewPost): Result<Unit>
}

interface ProfileRepository {
    suspend fun currentUser(): UserProfile
    suspend fun logout()
}

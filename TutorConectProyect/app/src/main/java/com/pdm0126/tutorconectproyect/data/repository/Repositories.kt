package com.pdm0126.tutorconectproyect.data.repository

import com.pdm0126.tutorconectproyect.data.model.Booking
import com.pdm0126.tutorconectproyect.data.model.ChatMessage
import com.pdm0126.tutorconectproyect.data.model.Post
import com.pdm0126.tutorconectproyect.data.model.User
import com.tutorconnect.domain.Resource
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun login(email: String, pass: String): Resource<User>
    suspend fun register(email: String, pass: String, name: String, isTutor: Boolean): Resource<User>
    suspend fun logout(): Resource<Unit>
}

interface TutorRepository {
    suspend fun getAllTutors(): Resource<List<User>>
    suspend fun getTutorById(tutorId: String): Resource<User>
}

interface PostRepository {
    suspend fun getAllPosts(): Resource<List<Post>>
    suspend fun createPost(post: Post): Resource<Unit>
}

interface BookingRepository {
    suspend fun createBooking(booking: Booking): Resource<Unit>
    // Un solo método inteligente: Si es tutor busca donde él es tutor, si es estudiante busca sus solicitudes
    suspend fun getBookingsForUser(userId: String, isTutor: Boolean): Resource<List<Booking>>
    suspend fun updateBookingStatus(bookingId: String, newStatus: String): Resource<Unit>
}

interface ChatRepository {
    suspend fun sendMessage(message: ChatMessage): Resource<Unit>
    // NOTA TÉCNICA: Usamos Flow en lugar de 'suspend' porque un chat
    // necesita emitir datos en tiempo real cada vez que llega un mensaje nuevo.
    fun getMessages(userId1: String, userId2: String): Flow<Resource<List<ChatMessage>>>
}

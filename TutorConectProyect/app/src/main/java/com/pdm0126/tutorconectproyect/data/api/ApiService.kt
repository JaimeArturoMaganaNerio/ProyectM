package com.tutorconnect.data.api


import com.tutorconnect.data.model.BookingRequest
import com.tutorconnect.data.model.NewPost
import com.tutorconnect.data.model.Subject
import com.tutorconnect.data.model.Tutor
import com.tutorconnect.data.model.TutoringSession
import com.tutorconnect.data.model.UserProfile
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): UserProfile

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): UserProfile

    @GET("api/dashboard/subjects")
    suspend fun getDashboardSubjects(): List<Subject>

    @GET("api/tutors")
    suspend fun getTutors(): List<Tutor>

    @GET("api/tutors/{id}")
    suspend fun getTutorById(@Path("id") id: String): Tutor

    @GET("api/calendar/sessions")
    suspend fun getSessions(): List<TutoringSession>

    @POST("api/booking")
    suspend fun bookSession(@Body request: BookingRequest): TutoringSession

    @POST("api/posts")
    suspend fun createPost(@Body post: NewPost): Unit

    @GET("api/profile")
    suspend fun getCurrentUser(): UserProfile
}

// DTOs
data class LoginRequest(val email: String, val password: String)

data class RegisterRequest(
    val fullName: String,
    val email: String,
    val password: String,
    val role: String
)

data class NewPost(val title: String, val content: String)

data class BookingRequest(val tutorId: String, val date: String, val time: String)
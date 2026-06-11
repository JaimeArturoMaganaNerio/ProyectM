package com.tutorconnect.data.repository


import com.tutorconnect.data.api.ApiService
import com.tutorconnect.data.api.LoginRequest
import com.tutorconnect.data.model.UserProfile

class DefaultAuthRepository(private val api: ApiService) : AuthRepository {
    override suspend fun login(email: String, password: String): Result<UserProfile> =
        runCatching { api.login(LoginRequest(email, password)) }

    override suspend fun loginWithMicrosoft(): Result<UserProfile> =
        runCatching { UserProfile("1", "Usuario", "user@uca.edu.sv", "Carrera") }
}
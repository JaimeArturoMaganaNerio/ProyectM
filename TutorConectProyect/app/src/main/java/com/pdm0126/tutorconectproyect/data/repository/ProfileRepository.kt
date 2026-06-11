package com.tutorconnect.data.repository


import com.tutorconnect.data.api.ApiService
import com.tutorconnect.data.model.UserProfile

class DefaultProfileRepository(private val api: ApiService) : ProfileRepository {
    override suspend fun currentUser(): UserProfile = api.getCurrentUser()
    override suspend fun logout() {}
}
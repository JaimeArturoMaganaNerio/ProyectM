package com.tutorconnect.data.repository


import com.tutorconnect.data.api.ApiService
import com.tutorconnect.data.model.Tutor

class DefaultTutorRepository(private val api: ApiService) : TutorRepository {
    override suspend fun tutors(): List<Tutor> = api.getTutors()
    override suspend fun tutorById(id: String): Tutor? = runCatching { api.getTutorById(id) }.getOrNull()
    override suspend fun faculties(): List<String> = emptyList()
    override suspend fun subjects(): List<String> = emptyList()
}
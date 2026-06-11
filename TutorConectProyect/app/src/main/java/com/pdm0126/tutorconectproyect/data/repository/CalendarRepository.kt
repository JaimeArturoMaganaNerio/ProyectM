package com.tutorconnect.data.repository


import com.tutorconnect.data.api.ApiService
import com.tutorconnect.data.model.TutoringSession

class DefaultCalendarRepository(private val api: ApiService) : CalendarRepository {
    override suspend fun sessions(): List<TutoringSession> = api.getSessions()
}
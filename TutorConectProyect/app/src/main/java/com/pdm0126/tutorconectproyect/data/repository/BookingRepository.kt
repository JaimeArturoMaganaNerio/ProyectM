package com.tutorconnect.data.repository

import com.tutorconnect.data.api.ApiService
import com.tutorconnect.data.model.BookingRequest
import com.tutorconnect.data.model.TutoringSession

class DefaultBookingRepository(private val api: ApiService) : BookingRepository {
    override suspend fun book(request: BookingRequest): Result<TutoringSession> =
        runCatching { api.bookSession(request) }
}
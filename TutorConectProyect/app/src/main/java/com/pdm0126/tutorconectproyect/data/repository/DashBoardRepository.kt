package com.tutorconnect.data.repository


import com.tutorconnect.data.api.ApiService
import com.tutorconnect.data.model.FeaturedPost
import com.tutorconnect.data.model.Subject

class DefaultDashboardRepository(private val api: ApiService) : DashboardRepository {
    override suspend fun tutorSubjects(): List<Subject> = api.getDashboardSubjects()
    override suspend fun additionalLoad(): List<Subject> = emptyList()
    override suspend fun featuredPosts(): List<FeaturedPost> = emptyList()
}
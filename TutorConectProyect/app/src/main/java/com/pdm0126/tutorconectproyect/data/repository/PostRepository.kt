package com.tutorconnect.data.repository


import com.tutorconnect.data.api.ApiService
import com.tutorconnect.data.model.NewPost

class DefaultPostRepository(private val api: ApiService) : PostRepository {
    override suspend fun publish(post: NewPost): Result<Unit> =
        runCatching { api.createPost(post) }
}
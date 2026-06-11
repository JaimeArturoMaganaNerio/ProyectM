package com.tutorconnect.data.repository


import com.tutorconnect.data.api.ApiService
import com.tutorconnect.data.model.ChatMessage

import java.util.UUID

class DefaultChatRepository(private val api: ApiService) : ChatRepository {
    override suspend fun messages(tutorId: String): List<ChatMessage> = emptyList()
    override suspend fun send(tutorId: String, text: String): ChatMessage =
        ChatMessage(UUID.randomUUID().toString(), text, true, "ahora")
}
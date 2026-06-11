package com.pdm0126.tutorconectproyect.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.pdm0126.tutorconectproyect.data.model.Post
import com.tutorconnect.domain.Resource
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebasePostRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : PostRepository {

    override suspend fun getAllPosts(): Resource<List<Post>> {
        return try {
            // Consultamos la colección "posts" y ordenamos por fecha (del más reciente al más antiguo)
            val snapshot = firestore.collection("posts")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            val posts = snapshot.toObjects(Post::class.java)
            Resource.Success(posts)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al cargar las publicaciones")
        }
    }

    override suspend fun createPost(post: Post): Resource<Unit> {
        return try {
            // Generamos una referencia con un ID único y automático de Firestore
            val documentRef = firestore.collection("posts").document()

            // Adjuntamos ese ID al modelo antes de guardarlo
            val newPost = post.copy(id = documentRef.id)

            documentRef.set(newPost).await()

            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al crear la publicación")
        }
    }
}
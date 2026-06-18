package com.pdm0126.tutorconnectproyect.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.snapshots
import com.pdm0126.tutorconnectproyect.data.model.User
import com.pdm0126.tutorconnectproyect.domain.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseTutorRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) : TutorRepository {

    override fun getAllTutors(): Flow<Resource<List<User>>> {
        return firestore.collection("users")
            .whereEqualTo("role", "TUTOR")
            .snapshots() // <-- ESCUCHA CAMBIOS EN VIVO
            .map { snapshot ->
                // Este bloque se ejecuta CADA VEZ que agregas/editas en la consola web
                val tutors = snapshot.toObjects(User::class.java)
                Resource.Success(tutors) as Resource<List<User>>
            }
            .catch { e ->
                // Manejo de errores del Flow
                emit(Resource.Error(e.message ?: "Error al cargar tutores en vivo"))
            }
    }

    override suspend fun getTutorById(tutorId: String): Resource<User> {
        return try {
            val snapshot = firestore.collection("users").document(tutorId).get().await()
            val tutor = snapshot.toObject(User::class.java)

            if (tutor != null) {
                Resource.Success(tutor)
            } else {
                Resource.Error("Tutor no encontrado")
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al cargar el perfil del tutor")
        }
    }
}
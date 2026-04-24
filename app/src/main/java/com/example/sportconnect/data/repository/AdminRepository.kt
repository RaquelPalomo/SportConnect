package com.example.sportconnect.data.repository

import com.example.sportconnect.data.model.Actividad
import com.example.sportconnect.data.model.Usuario
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AdminRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getTodosUsuarios(): Result<List<Usuario>> {
        return try {
            val snapshot = db.collection("usuarios").get().await()
            val usuarios = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Usuario::class.java)?.copy(uid = doc.id)
            }
            Result.success(usuarios)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getTodasActividades(): Result<List<Actividad>> {
        return try {
            val snapshot = db.collection("actividades").get().await()
            val actividades = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Actividad::class.java)?.copy(id = doc.id)
            }
            Result.success(actividades)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarActividad(actividadId: String): Result<Unit> {
        return try {
            db.collection("actividades").document(actividadId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun eliminarUsuario(uid: String): Result<Unit> {
        return try {
            db.collection("usuarios").document(uid).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
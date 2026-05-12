package com.example.sportconnect.data.repository

import com.example.sportconnect.data.model.Actividad
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ProfesionalRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    suspend fun crearActividad(
        tipo: String,
        descripcion: String,
        lugar: String,
        poblacion: String,
        fecha: Timestamp,
        duracionMinutos: Int,
        maxParticipantes: Int
    ): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val nombreDoc = db.collection("usuarios").document(uid).get().await()
            val nombreProfesor = nombreDoc.getString("nombre") ?: "Profesional"

            val actividad = Actividad(
                tipo = tipo,
                descripcion = descripcion,
                profesorId = uid,
                profesorNombre = nombreProfesor,
                lugar = lugar,
                poblacion = poblacion,
                fecha = fecha,
                duracionMinutos = duracionMinutos,
                maxParticipantes = maxParticipantes,
                participantesActuales = 0,
                activa = true
            )

            db.collection("actividades").add(actividad).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun editarActividad(
        actividadId: String,
        tipo: String,
        descripcion: String,
        lugar: String,
        poblacion: String,
        fecha: Timestamp,
        duracionMinutos: Int,
        maxParticipantes: Int
    ): Result<Unit> {
        return try {
            db.collection("actividades").document(actividadId).update(
                mapOf(
                    "tipo" to tipo,
                    "descripcion" to descripcion,
                    "lugar" to lugar,
                    "poblacion" to poblacion,
                    "fecha" to fecha,
                    "duracionMinutos" to duracionMinutos,
                    "maxParticipantes" to maxParticipantes
                )
            ).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun cancelarActividad(actividadId: String): Result<Unit> {
        return try {
            db.collection("actividades").document(actividadId)
                .update("activa", false)
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMisActividadesPendientes(): Result<List<Actividad>> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val ahora = Timestamp.now()

            val snapshot = db.collection("actividades")
                .whereEqualTo("profesorId", uid)
                .whereEqualTo("activa", true)
                .whereGreaterThan("fecha", ahora)
                .get()
                .await()

            val actividades = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Actividad::class.java)?.copy(id = doc.id)
            }

            Result.success(actividades)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMisActividadesCompletadas(): Result<List<Actividad>> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val ahora = Timestamp.now()

            val snapshot = db.collection("actividades")
                .whereEqualTo("profesorId", uid)
                .whereEqualTo("activa", true)
                .whereLessThanOrEqualTo("fecha", ahora)
                .get()
                .await()

            val actividades = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Actividad::class.java)?.copy(id = doc.id)
            }

            Result.success(actividades)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMisActividadesCanceladas(): Result<List<Actividad>> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val snapshot = db.collection("actividades")
                .whereEqualTo("profesorId", uid)
                .whereEqualTo("activa", false)
                .get()
                .await()

            val actividades = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Actividad::class.java)?.copy(id = doc.id)
            }

            Result.success(actividades)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getInscritosActividad(actividadId: String): Result<Int> {
        return try {
            val snapshot = db.collection("reservas")
                .whereEqualTo("actividadId", actividadId)
                .get()
                .await()
            Result.success(snapshot.size())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
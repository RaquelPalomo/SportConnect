package com.example.sportconnect.data.repository

import com.example.sportconnect.data.model.Actividad
import com.example.sportconnect.data.model.Reserva
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.Date

class ActividadRepository {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Obtiene todas las actividades activas, con plazas disponibles y fecha futura
    suspend fun getActividades(): Result<List<Actividad>> {
        return try {
            val snapshot = db.collection("actividades")
                .whereEqualTo("activa", true)
                .get()
                .await()

            val ahora = Date()

            val actividades = snapshot.documents.mapNotNull { doc ->
                doc.toObject(Actividad::class.java)?.copy(id = doc.id)
            }.filter {
                it.participantesActuales < it.maxParticipantes &&
                        it.fecha?.toDate()?.after(ahora) == true
            }

            Result.success(actividades)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Reserva una plaza en una actividad
    suspend fun reservarActividad(actividadId: String): Result<Unit> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            // Comprueba si ya tiene reserva
            val reservaExistente = db.collection("reservas")
                .whereEqualTo("usuarioId", uid)
                .whereEqualTo("actividadId", actividadId)
                .get()
                .await()

            if (!reservaExistente.isEmpty) {
                return Result.failure(Exception("Ya tienes una reserva para esta actividad"))
            }

            // Obtiene la actividad para verificar aforo
            val actividadDoc = db.collection("actividades")
                .document(actividadId)
                .get()
                .await()

            val actividad = actividadDoc.toObject(Actividad::class.java)
                ?: return Result.failure(Exception("Actividad no encontrada"))

            if (actividad.participantesActuales >= actividad.maxParticipantes) {
                return Result.failure(Exception("No quedan plazas disponibles"))
            }

            // Crea la reserva
            val reserva = Reserva(
                usuarioId = uid,
                actividadId = actividadId,
                fechaReserva = com.google.firebase.Timestamp.now()
            )
            db.collection("reservas").add(reserva).await()

            // Incrementa participantes
            db.collection("actividades").document(actividadId)
                .update("participantesActuales",
                    actividad.participantesActuales + 1)
                .await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Obtiene las reservas del usuario actual
    suspend fun getMisReservas(): Result<List<Actividad>> {
        return try {
            val uid = auth.currentUser?.uid
                ?: return Result.failure(Exception("Usuario no autenticado"))

            val reservasSnapshot = db.collection("reservas")
                .whereEqualTo("usuarioId", uid)
                .get()
                .await()

            val actividadIds = reservasSnapshot.documents.map {
                it.getString("actividadId") ?: ""
            }.filter { it.isNotEmpty() }

            if (actividadIds.isEmpty()) return Result.success(emptyList())

            val actividades = actividadIds.map { id ->
                val doc = db.collection("actividades").document(id).get().await()
                doc.toObject(Actividad::class.java)?.copy(id = doc.id)
            }.filterNotNull()

            Result.success(actividades)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
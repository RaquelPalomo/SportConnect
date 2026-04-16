package com.example.sportconnect.data.repository

import com.example.sportconnect.data.model.Usuario
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await


class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    fun getUsuarioActual() = auth.currentUser

    suspend fun login(email: String, password: String): Result<String> {
        return try {
            val resultado = auth.signInWithEmailAndPassword(email, password).await()
            val uid = resultado.user?.uid
                ?: return Result.failure(Exception("Error al obtener usuario"))

            val doc = db.collection("usuarios").document(uid).get().await()
            val rol = doc.getString("rol") ?: "basico"

            Result.success(rol)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registro(
        nombre: String,
        email: String,
        password: String,
        rol: String
    ): Result<Unit> {
        return try {
            val resultado = auth.createUserWithEmailAndPassword(email, password).await()
            val uid = resultado.user?.uid
                ?: return Result.failure(Exception("Error al crear usuario"))

            val usuario = Usuario(uid = uid, nombre = nombre, email = email, rol = rol)
            db.collection("usuarios").document(uid).set(usuario).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun cerrarSesion() {
        auth.signOut()
    }
    suspend fun recuperarPassword(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
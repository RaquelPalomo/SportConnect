package com.example.sportconnect.data.model

import com.google.firebase.Timestamp

data class Actividad(
    val id: String = "",
    val tipo: String = "",
    val descripcion: String = "",
    val profesorId: String = "",
    val profesorNombre: String = "",
    val lugar: String = "",
    val poblacion: String = "",
    val fecha: Timestamp? = null,
    val duracionMinutos: Int = 0,
    val maxParticipantes: Int = 0,
    val participantesActuales: Int = 0,
    val activa: Boolean = true
)
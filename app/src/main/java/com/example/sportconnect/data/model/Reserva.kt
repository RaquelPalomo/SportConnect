package com.example.sportconnect.data.model

import com.google.firebase.Timestamp

data class Reserva(
    val id: String = "",
    val usuarioId: String = "",
    val actividadId: String = "",
    val fechaReserva: Timestamp? = null
)
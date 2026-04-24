package com.example.sportconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportconnect.data.model.Actividad
import com.example.sportconnect.data.repository.ProfesionalRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.launch
import java.util.Date

sealed class ProfesionalUiState {
    object Idle : ProfesionalUiState()
    object Loading : ProfesionalUiState()
    data class Success(val actividades: List<Actividad>) : ProfesionalUiState()
    data class Error(val mensaje: String) : ProfesionalUiState()
    object ActividadCreada : ProfesionalUiState()
}

class ProfesionalViewModel : ViewModel() {

    private val repository = ProfesionalRepository()

    private val _uiState = MutableLiveData<ProfesionalUiState>(ProfesionalUiState.Idle)
    val uiState: LiveData<ProfesionalUiState> = _uiState

    private var todasLasActividades: List<Actividad> = emptyList()

    fun crearActividad(
        tipo: String,
        descripcion: String,
        lugar: String,
        poblacion: String,
        fecha: Date,
        duracionMinutos: Int,
        maxParticipantes: Int
    ) {
        if (tipo.isBlank() || descripcion.isBlank() || lugar.isBlank() || poblacion.isBlank()) {
            _uiState.value = ProfesionalUiState.Error("Rellena todos los campos")
            return
        }
        if (duracionMinutos <= 0) {
            _uiState.value = ProfesionalUiState.Error("La duración debe ser mayor que 0")
            return
        }
        if (maxParticipantes <= 0) {
            _uiState.value = ProfesionalUiState.Error("El número de participantes debe ser mayor que 0")
            return
        }

        _uiState.value = ProfesionalUiState.Loading
        viewModelScope.launch {
            val result = repository.crearActividad(
                tipo, descripcion, lugar, poblacion,
                Timestamp(fecha), duracionMinutos, maxParticipantes
            )
            _uiState.value = if (result.isSuccess) {
                ProfesionalUiState.ActividadCreada
            } else {
                ProfesionalUiState.Error("Error al crear la actividad")
            }
        }
    }

    fun cargarMisActividades() {
        _uiState.value = ProfesionalUiState.Loading
        viewModelScope.launch {
            val result = repository.getMisActividades()
            if (result.isSuccess) {
                todasLasActividades = result.getOrDefault(emptyList())
                _uiState.value = ProfesionalUiState.Success(todasLasActividades)
            } else {
                _uiState.value = ProfesionalUiState.Error("Error al cargar tus actividades")
            }
        }
    }

    fun filtrarPorEstado(soloFuturas: Boolean) {
        val ahora = Date()
        val filtradas = if (soloFuturas) {
            todasLasActividades.filter { actividad ->
                actividad.fecha?.toDate()?.after(ahora) == true
            }
        } else {
            todasLasActividades.filter { actividad ->
                actividad.fecha?.toDate()?.before(ahora) == true
            }
        }
        _uiState.value = ProfesionalUiState.Success(filtradas)
    }

    fun resetState() {
        _uiState.value = ProfesionalUiState.Idle
    }
}
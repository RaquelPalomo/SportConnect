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

    // Carga directamente las pendientes al iniciar
    fun cargarMisActividades() {
        filtrarPorEstado(soloFuturas = true)
    }

    // Consulta Firestore directamente según el estado
    fun filtrarPorEstado(soloFuturas: Boolean) {
        _uiState.value = ProfesionalUiState.Loading
        viewModelScope.launch {
            val result = if (soloFuturas) {
                repository.getMisActividadesPendientes()
            } else {
                repository.getMisActividadesCompletadas()
            }
            if (result.isSuccess) {
                _uiState.value = ProfesionalUiState.Success(result.getOrDefault(emptyList()))
            } else {
                _uiState.value = ProfesionalUiState.Error("Error al cargar las actividades")
            }
        }
    }

    fun resetState() {
        _uiState.value = ProfesionalUiState.Idle
    }
}
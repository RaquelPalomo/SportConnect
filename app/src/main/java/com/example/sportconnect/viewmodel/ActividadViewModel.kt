package com.example.sportconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportconnect.data.model.Actividad
import com.example.sportconnect.data.repository.ActividadRepository
import kotlinx.coroutines.launch

sealed class ActividadUiState {
    object Idle : ActividadUiState()
    object Loading : ActividadUiState()
    data class Success(val actividades: List<Actividad>) : ActividadUiState()
    data class Error(val mensaje: String) : ActividadUiState()
    object ReservaExitosa : ActividadUiState()
}

class ActividadViewModel : ViewModel() {

    private val repository = ActividadRepository()

    private val _uiState = MutableLiveData<ActividadUiState>(ActividadUiState.Idle)
    val uiState: LiveData<ActividadUiState> = _uiState

    private val _actividadDetalle = MutableLiveData<Actividad?>()
    val actividadDetalle: LiveData<Actividad?> = _actividadDetalle

    // Lista completa sin filtrar
    private var todasLasActividades: List<Actividad> = emptyList()

    fun cargarActividades() {
        _uiState.value = ActividadUiState.Loading
        viewModelScope.launch {
            val result = repository.getActividades()
            if (result.isSuccess) {
                todasLasActividades = result.getOrDefault(emptyList())
                _uiState.value = ActividadUiState.Success(todasLasActividades)
            } else {
                _uiState.value = ActividadUiState.Error("Error al cargar actividades")
            }
        }
    }

    fun filtrarActividades(tipo: String = "", poblacion: String = "") {
        val filtradas = todasLasActividades.filter { actividad ->
            (tipo.isEmpty() || actividad.tipo.contains(tipo, ignoreCase = true)) &&
                    (poblacion.isEmpty() || actividad.poblacion.contains(poblacion, ignoreCase = true))
        }
        _uiState.value = ActividadUiState.Success(filtradas)
    }

    fun seleccionarActividad(actividad: Actividad) {
        _actividadDetalle.value = actividad
    }

    fun reservarActividad(actividadId: String) {
        _uiState.value = ActividadUiState.Loading
        viewModelScope.launch {
            val result = repository.reservarActividad(actividadId)
            _uiState.value = if (result.isSuccess) {
                ActividadUiState.ReservaExitosa
            } else {
                ActividadUiState.Error(result.exceptionOrNull()?.message ?: "Error al reservar")
            }
        }
    }

    fun cargarMisReservas() {
        _uiState.value = ActividadUiState.Loading
        viewModelScope.launch {
            val result = repository.getMisReservas()
            if (result.isSuccess) {
                _uiState.value = ActividadUiState.Success(result.getOrDefault(emptyList()))
            } else {
                _uiState.value = ActividadUiState.Error("Error al cargar tus reservas")
            }
        }
    }

    fun resetState() {
        _uiState.value = ActividadUiState.Idle
    }
}
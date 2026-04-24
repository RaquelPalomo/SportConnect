package com.example.sportconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportconnect.data.model.Actividad
import com.example.sportconnect.data.model.Usuario
import com.example.sportconnect.data.repository.AdminRepository
import kotlinx.coroutines.launch

sealed class AdminUiState {
    object Idle : AdminUiState()
    object Loading : AdminUiState()
    data class UsuariosSuccess(val usuarios: List<Usuario>) : AdminUiState()
    data class ActividadesSuccess(val actividades: List<Actividad>) : AdminUiState()
    data class Error(val mensaje: String) : AdminUiState()
    object EliminacionExitosa : AdminUiState()
}

class AdminViewModel : ViewModel() {

    private val repository = AdminRepository()

    private val _uiState = MutableLiveData<AdminUiState>(AdminUiState.Idle)
    val uiState: LiveData<AdminUiState> = _uiState

    fun cargarUsuarios() {
        _uiState.value = AdminUiState.Loading
        viewModelScope.launch {
            val result = repository.getTodosUsuarios()
            _uiState.value = if (result.isSuccess) {
                AdminUiState.UsuariosSuccess(result.getOrDefault(emptyList()))
            } else {
                AdminUiState.Error("Error al cargar usuarios")
            }
        }
    }

    fun cargarActividades() {
        _uiState.value = AdminUiState.Loading
        viewModelScope.launch {
            val result = repository.getTodasActividades()
            _uiState.value = if (result.isSuccess) {
                AdminUiState.ActividadesSuccess(result.getOrDefault(emptyList()))
            } else {
                AdminUiState.Error("Error al cargar actividades")
            }
        }
    }

    fun eliminarActividad(actividadId: String) {
        _uiState.value = AdminUiState.Loading
        viewModelScope.launch {
            val result = repository.eliminarActividad(actividadId)
            _uiState.value = if (result.isSuccess) {
                AdminUiState.EliminacionExitosa
            } else {
                AdminUiState.Error("Error al eliminar la actividad")
            }
        }
    }

    fun eliminarUsuario(uid: String) {
        _uiState.value = AdminUiState.Loading
        viewModelScope.launch {
            val result = repository.eliminarUsuario(uid)
            _uiState.value = if (result.isSuccess) {
                AdminUiState.EliminacionExitosa
            } else {
                AdminUiState.Error("Error al eliminar el usuario")
            }
        }
    }
}
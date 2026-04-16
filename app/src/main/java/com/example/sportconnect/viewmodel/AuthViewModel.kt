package com.example.sportconnect.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sportconnect.data.repository.AuthRepository
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val rol: String) : AuthUiState()
    data class Error(val mensaje: String) : AuthUiState()
}

class AuthViewModel : ViewModel() {

    private val repository = AuthRepository()

    private val _uiState = MutableLiveData<AuthUiState>(AuthUiState.Idle)
    val uiState: LiveData<AuthUiState> = _uiState

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Rellena todos los campos")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.login(email, password)
            _uiState.value = if (result.isSuccess) {
                AuthUiState.Success(result.getOrDefault("basico"))
            } else {
                AuthUiState.Error("Email o contraseña incorrectos")
            }
        }
    }

    fun registro(nombre: String, email: String, password: String, rol: String) {
        if (nombre.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Rellena todos los campos")
            return
        }
        if (password.length < 6) {
            _uiState.value = AuthUiState.Error("La contraseña debe tener al menos 6 caracteres")
            return
        }
        _uiState.value = AuthUiState.Loading
        viewModelScope.launch {
            val result = repository.registro(nombre, email, password, rol)
            _uiState.value = if (result.isSuccess) {
                AuthUiState.Success(rol)
            } else {
                AuthUiState.Error("No se pudo crear la cuenta. El email puede estar en uso.")
            }
        }
    }

    fun resetState() {
        _uiState.value = AuthUiState.Idle
    }
}
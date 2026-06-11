package com.itba.homecore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.itba.homecore.data.api.SessionEvents
import com.itba.homecore.data.model.User
import com.itba.homecore.data.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User? = null) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AuthRepository(application)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /** null = chequeando sesión al arrancar, true/false = decisión tomada. */
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    init {
        viewModelScope.launch {
            val hasSession = repository.restoreSession()
            _isLoggedIn.value = hasSession
        }
        // Si cualquier llamada a la API recibe 401, la red emite acá y limpiamos sesión.
        viewModelScope.launch {
            SessionEvents.unauthorized.collect {
                repository.logout()
                _isLoggedIn.value = false
                _uiState.value = AuthUiState.Error("Tu sesión expiró. Iniciá sesión nuevamente.")
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Completá todos los campos")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.login(email, password).fold(
                onSuccess = { user ->
                    _isLoggedIn.value = true
                    _uiState.value = AuthUiState.Success(user)
                },
                onFailure = { e -> _uiState.value = AuthUiState.Error(e.message ?: "Error al iniciar sesión") }
            )
        }
    }

    fun register(name: String, lastName: String, email: String, password: String, confirmPassword: String) {
        if (name.isBlank() || lastName.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = AuthUiState.Error("Completá todos los campos")
            return
        }
        if (password != confirmPassword) {
            _uiState.value = AuthUiState.Error("Las contraseñas no coinciden")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.register(name, lastName, email, password).fold(
                onSuccess = { _uiState.value = AuthUiState.Success() },
                onFailure = { e -> _uiState.value = AuthUiState.Error(e.message ?: "Error al registrar") }
            )
        }
    }

    fun logout() {
        viewModelScope.launch {
            repository.logout()
            _isLoggedIn.value = false
            _uiState.value = AuthUiState.Idle
        }
    }

    fun clearState() {
        _uiState.value = AuthUiState.Idle
    }
}

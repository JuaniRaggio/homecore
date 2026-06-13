package com.itba.homecore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.itba.homecore.data.api.SessionEvents
import com.itba.homecore.data.model.User
import com.itba.homecore.data.repository.EmailAlreadyRegisteredException
import com.itba.homecore.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

sealed class AuthUiState {
    object Idle : AuthUiState()
    object Loading : AuthUiState()
    data class Success(val user: User? = null) : AuthUiState()
    /** Registro OK: el código ya fue enviado por email, falta verificar. */
    object RegistrationPending : AuthUiState()
    /** Cuenta verificada pero sin auto-login: el usuario debe iniciar sesión. */
    object Verified : AuthUiState()
    /** El email ya estaba registrado: hay que mandar al usuario a la pantalla de inicio (Login). */
    data class AlreadyRegistered(val message: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

/** Estado del reenvío de código, separado de [AuthUiState] para no pisar el botón de verificar. */
sealed class ResendState {
    object Idle : ResendState()
    object Sending : ResendState()
    object Sent : ResendState()
    data class Error(val message: String) : ResendState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppModule.authRepository(application)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /** null = checking the session at startup, true/false = decision made. */
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    /** Email al que se envió el código, para mostrarlo en la pantalla de verificación. */
    private val _pendingEmail = MutableStateFlow<String?>(null)
    val pendingEmail: StateFlow<String?> = _pendingEmail.asStateFlow()

    /** Contraseña retenida en memoria entre registro y verificación para el auto-login. */
    private var pendingPassword: String? = null

    private val _resendState = MutableStateFlow<ResendState>(ResendState.Idle)
    val resendState: StateFlow<ResendState> = _resendState.asStateFlow()

    init {
        viewModelScope.launch {
            val hasSession = repository.restoreSession()
            _isLoggedIn.value = hasSession
        }
        // If any API call gets a 401, the network layer emits here and we clear the session.
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
                onSuccess = {
                    // El registro ya disparó el envío del código: pasamos a verificar.
                    _pendingEmail.value = email
                    pendingPassword = password
                    _resendState.value = ResendState.Idle
                    _uiState.value = AuthUiState.RegistrationPending
                },
                onFailure = { e ->
                    _uiState.value = if (e is EmailAlreadyRegisteredException)
                        AuthUiState.AlreadyRegistered(e.message ?: "Este email ya está registrado. Iniciá sesión.")
                    else
                        AuthUiState.Error(e.message ?: "Error al registrar")
                }
            )
        }
    }

    fun verifyAccount(code: String) {
        if (code.isBlank()) {
            _uiState.value = AuthUiState.Error("Ingresá el código de verificación")
            return
        }
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            repository.verifyAccount(code).fold(
                onSuccess = {
                    val email = _pendingEmail.value
                    val password = pendingPassword
                    if (email != null && password != null) {
                        // Auto-login con las credenciales del registro (igual que en web).
                        repository.login(email, password).fold(
                            onSuccess = { user ->
                                clearPending()
                                _isLoggedIn.value = true
                                _uiState.value = AuthUiState.Success(user)
                            },
                            onFailure = {
                                // Verificó pero no pudo entrar solo: que inicie sesión a mano.
                                clearPending()
                                _uiState.value = AuthUiState.Verified
                            }
                        )
                    } else {
                        _uiState.value = AuthUiState.Verified
                    }
                },
                onFailure = { e -> _uiState.value = AuthUiState.Error(e.message ?: "Código incorrecto") }
            )
        }
    }

    /** Reenvía el código de verificación al email pendiente (botón "Reenviar código"). */
    fun resendCode() {
        if (_resendState.value is ResendState.Sending) return
        val email = _pendingEmail.value
        if (email.isNullOrBlank()) {
            _resendState.value = ResendState.Error("No hay un email al que reenviar el código")
            return
        }
        viewModelScope.launch {
            _resendState.value = ResendState.Sending
            repository.sendVerification(email).fold(
                onSuccess = { _resendState.value = ResendState.Sent },
                onFailure = { e -> _resendState.value = ResendState.Error(e.message ?: "No se pudo reenviar el código") }
            )
        }
    }

    private fun clearPending() {
        _pendingEmail.value = null
        pendingPassword = null
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

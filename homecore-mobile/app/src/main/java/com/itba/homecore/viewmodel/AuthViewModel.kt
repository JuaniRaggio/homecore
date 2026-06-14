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
    /** Registration OK: the code was already emailed; verification is pending. */
    object RegistrationPending : AuthUiState()
    /** Account verified but no auto-login: the user must sign in. */
    object Verified : AuthUiState()
    /** Email was already registered: send the user to the Login screen. */
    data class AlreadyRegistered(val message: String) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
}

/** Resend-code state, separate from [AuthUiState] so it doesn't clobber the verify button. */
sealed class ResendState {
    object Idle : ResendState()
    object Sending : ResendState()
    object Sent : ResendState()
    data class Error(val message: String) : ResendState()
}

/** Forgot-password flow state, independent of [AuthUiState] (it has its own screen). */
sealed class RecoverState {
    object Idle : RecoverState()
    object Loading : RecoverState()
    /** The recovery code was emailed: move to the "enter code + new password" step. */
    object CodeSent : RecoverState()
    /** Password reset OK: show success and let the user go to Login. */
    object Done : RecoverState()
    data class Error(val message: String) : RecoverState()
}

/** Change-password state (from the Profile tab while logged in). */
sealed class ChangePasswordState {
    object Idle : ChangePasswordState()
    object Loading : ChangePasswordState()
    object Done : ChangePasswordState()
    data class Error(val message: String) : ChangePasswordState()
}

class AuthViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = AppModule.authRepository(application)

    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    /** null = checking the session at startup, true/false = decision made. */
    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> = _isLoggedIn.asStateFlow()

    /** Email the code was sent to, shown on the verification screen. */
    private val _pendingEmail = MutableStateFlow<String?>(null)
    val pendingEmail: StateFlow<String?> = _pendingEmail.asStateFlow()

    /** Password held in memory between registration and verification for the auto-login. */
    private var pendingPassword: String? = null

    private val _resendState = MutableStateFlow<ResendState>(ResendState.Idle)
    val resendState: StateFlow<ResendState> = _resendState.asStateFlow()

    private val _recoverState = MutableStateFlow<RecoverState>(RecoverState.Idle)
    val recoverState: StateFlow<RecoverState> = _recoverState.asStateFlow()

    private val _changePasswordState = MutableStateFlow<ChangePasswordState>(ChangePasswordState.Idle)
    val changePasswordState: StateFlow<ChangePasswordState> = _changePasswordState.asStateFlow()

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
                    // Registration already triggered the code email: move to verification.
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
                        // Auto-login with the registration credentials (same as on web).
                        repository.login(email, password).fold(
                            onSuccess = { user ->
                                clearPending()
                                _isLoggedIn.value = true
                                _uiState.value = AuthUiState.Success(user)
                            },
                            onFailure = {
                                // Verified but auto-login failed: have the user sign in manually.
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

    /** Resends the verification code to the pending email (the "Resend code" button). */
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

    /** Step 1 of forgot-password: request the recovery code by email. */
    fun forgotPassword(email: String) {
        if (email.isBlank()) {
            _recoverState.value = RecoverState.Error("Ingresá tu email")
            return
        }
        viewModelScope.launch {
            _recoverState.value = RecoverState.Loading
            repository.forgotPassword(email).fold(
                onSuccess = { _recoverState.value = RecoverState.CodeSent },
                onFailure = { e -> _recoverState.value = RecoverState.Error(e.message ?: "No se pudo enviar el código") }
            )
        }
    }

    /** Step 2 of forgot-password: set the new password using the emailed code. */
    fun resetPassword(code: String, newPassword: String, confirmPassword: String) {
        when {
            code.isBlank() ->
                _recoverState.value = RecoverState.Error("Ingresá el código de recuperación")
            newPassword.length < 8 ->
                _recoverState.value = RecoverState.Error("La contraseña debe tener al menos 8 caracteres")
            newPassword != confirmPassword ->
                _recoverState.value = RecoverState.Error("Las contraseñas no coinciden")
            else -> viewModelScope.launch {
                _recoverState.value = RecoverState.Loading
                repository.resetPassword(code, newPassword).fold(
                    onSuccess = { _recoverState.value = RecoverState.Done },
                    onFailure = { e -> _recoverState.value = RecoverState.Error(e.message ?: "No se pudo restablecer la contraseña") }
                )
            }
        }
    }

    fun clearRecover() {
        _recoverState.value = RecoverState.Idle
    }

    /** Changes the password of the logged-in user (Profile tab). */
    fun changePassword(currentPassword: String, newPassword: String, confirmPassword: String) {
        when {
            currentPassword.isBlank() ->
                _changePasswordState.value = ChangePasswordState.Error("Ingresá tu contraseña actual")
            newPassword.length < 8 ->
                _changePasswordState.value = ChangePasswordState.Error("La nueva contraseña debe tener al menos 8 caracteres")
            newPassword != confirmPassword ->
                _changePasswordState.value = ChangePasswordState.Error("Las contraseñas no coinciden")
            else -> viewModelScope.launch {
                _changePasswordState.value = ChangePasswordState.Loading
                repository.changePassword(currentPassword, newPassword).fold(
                    onSuccess = { _changePasswordState.value = ChangePasswordState.Done },
                    onFailure = { e -> _changePasswordState.value = ChangePasswordState.Error(e.message ?: "No se pudo cambiar la contraseña") }
                )
            }
        }
    }

    fun clearChangePassword() {
        _changePasswordState.value = ChangePasswordState.Idle
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

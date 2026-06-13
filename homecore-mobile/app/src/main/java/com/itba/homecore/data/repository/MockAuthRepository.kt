package com.itba.homecore.data.repository

import com.itba.homecore.data.model.User
import kotlinx.coroutines.delay

/**
 * Prototype authentication: accepts any credentials so the app flow can be tested
 * without a backend. It does not persist the session, so reopening shows Login.
 */
class MockAuthRepository : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        delay(300)
        require(email.isNotBlank() && password.isNotBlank()) { "Completá email y contraseña" }
        User(id = "mock-user", name = "Maria Fernandez", email = email)
    }

    override suspend fun register(name: String, lastName: String, email: String, password: String): Result<Unit> = runCatching {
        delay(300)
        // Sentinel to exercise the "email already registered -> go to Login" flow without a backend.
        if (email.trim().equals("registrado@homecore.com", ignoreCase = true)) {
            throw EmailAlreadyRegisteredException("Este email ya está registrado. Iniciá sesión.")
        }
    }

    override suspend fun logout() {
        // No persistent state to clear in mock mode.
    }

    override suspend fun restoreSession(): Boolean = false

    override suspend fun verifyAccount(code: String): Result<Unit> = runCatching {
        delay(300)
        require(code.isNotBlank()) { "Ingresá el código de verificación" }
    }

    override suspend fun sendVerification(email: String): Result<Unit> = runCatching {
        delay(300)
        require(email.isNotBlank()) { "Ingresá tu email" }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> = runCatching {
        delay(300)
        require(email.isNotBlank()) { "Ingresá tu email" }
    }

    override suspend fun resetPassword(code: String, newPassword: String): Result<Unit> = runCatching {
        delay(300)
        require(code.isNotBlank()) { "Ingresá el código" }
        require(newPassword.length >= 6) { "La contraseña debe tener al menos 6 caracteres" }
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> = runCatching {
        delay(300)
        require(oldPassword.isNotBlank()) { "Ingresá tu contraseña actual" }
        require(newPassword.length >= 6) { "La contraseña debe tener al menos 6 caracteres" }
    }

    override suspend fun getProfile(): Result<User> = runCatching {
        delay(200)
        User(id = "mock-user", name = "Maria Fernandez", email = "maria@example.com")
    }
}

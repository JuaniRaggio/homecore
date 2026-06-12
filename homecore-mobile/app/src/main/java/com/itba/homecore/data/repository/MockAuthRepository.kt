package com.itba.homecore.data.repository

import com.itba.homecore.data.model.User
import kotlinx.coroutines.delay

/**
 * Autenticación de prototipo: acepta cualquier credencial para poder testear el
 * flujo de la app sin backend. No persiste sesión, así que al reabrir muestra Login.
 */
class MockAuthRepository : AuthRepository {

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        delay(300)
        require(email.isNotBlank() && password.isNotBlank()) { "Completá email y contraseña" }
        User(id = "mock-user", name = "Maria Fernandez", email = email)
    }

    override suspend fun register(name: String, lastName: String, email: String, password: String): Result<Unit> = runCatching {
        delay(300)
        // Sentinela para probar el flujo "email ya registrado → ir a Login" sin backend.
        if (email.trim().equals("registrado@homecore.com", ignoreCase = true)) {
            throw EmailAlreadyRegisteredException("Este email ya está registrado. Iniciá sesión.")
        }
    }

    override suspend fun sendVerification(email: String): Result<Unit> = runCatching {
        delay(300)
    }

    override suspend fun verifyAccount(code: String): Result<Unit> = runCatching {
        delay(300)
        require(code.isNotBlank()) { "Ingresá el código de verificación" }
    }

    override suspend fun logout() {
        // Sin estado persistente que limpiar en modo mock.
    }

    override suspend fun restoreSession(): Boolean = false
}

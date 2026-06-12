package com.itba.homecore.data.repository

import com.itba.homecore.data.model.User

/** El email ya tiene una cuenta asociada: el flujo debe llevar al usuario a iniciar sesión. */
class EmailAlreadyRegisteredException(message: String) : Exception(message)

/**
 * Contrato de autenticación. La implementación mock ([MockAuthRepository]) permite
 * entrar con cualquier credencial para testear la UI sin backend; la real
 * ([RemoteAuthRepository]) usa la API HCI + persistencia en DataStore.
 */
interface AuthRepository {
    suspend fun login(email: String, password: String): Result<User>
    suspend fun register(name: String, lastName: String, email: String, password: String): Result<Unit>
    suspend fun sendVerification(email: String): Result<Unit>
    suspend fun verifyAccount(code: String): Result<Unit>
    suspend fun logout()
    suspend fun restoreSession(): Boolean
}

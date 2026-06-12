package com.itba.homecore.data.repository

import android.content.Context
import com.itba.homecore.data.api.ApiClient
import com.itba.homecore.data.local.SessionManager
import com.itba.homecore.data.model.LoginRequest
import com.itba.homecore.data.model.RegisterRequest
import com.itba.homecore.data.model.SendVerificationRequest
import com.itba.homecore.data.model.User
import com.itba.homecore.data.model.VerifyAccountRequest
import retrofit2.HttpException

/**
 * Autenticación real contra la API HCI + persistencia de sesión en DataStore.
 */
class RemoteAuthRepository(context: Context) : AuthRepository {
    private val api     = ApiClient.authApi
    private val session = SessionManager(context)

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        try {
            val auth  = api.login(LoginRequest(email, password))
            val token = auth.token ?: throw Exception("No se recibió token")

            ApiClient.setToken(token)
            session.saveToken(token)

            val user = auth.user ?: User(email = email)
            session.saveUserInfo(user.id, user.fullName, user.email)
            user
        } catch (e: HttpException) {
            throw Exception(parseError(e) ?: "Email o contraseña incorrectos")
        }
    }

    /**
     * Registro en dos pasos: primero crea la cuenta y luego dispara el envío del código
     * de verificación por email (/send-verification), igual que el flujo de web.
     */
    override suspend fun register(name: String, lastName: String, email: String, password: String): Result<Unit> = runCatching {
        val fullName = if (lastName.isBlank()) name else "$name $lastName"
        try {
            api.register(RegisterRequest(fullName, email, password))
        } catch (e: HttpException) {
            // 409 ⇒ el email ya tiene cuenta: lo señalamos para mandar al usuario a iniciar sesión.
            if (e.code() == 409) {
                throw EmailAlreadyRegisteredException("Este email ya está registrado. Iniciá sesión.")
            }
            throw Exception(parseError(e) ?: "Error al registrar (${e.code()})")
        }
        // Cuenta creada: enviamos el código de verificación al email.
        sendVerification(email).getOrThrow()
    }

    override suspend fun sendVerification(email: String): Result<Unit> = runCatching {
        try {
            api.sendVerification(SendVerificationRequest(email))
            Unit
        } catch (e: HttpException) {
            throw Exception(parseError(e) ?: "No se pudo enviar el código (${e.code()}). Intentá de nuevo.")
        }
    }

    override suspend fun verifyAccount(code: String): Result<Unit> = runCatching {
        try {
            api.verifyAccount(VerifyAccountRequest(code))
            Unit
        } catch (e: HttpException) {
            throw Exception(parseError(e) ?: "Código de verificación inválido")
        }
    }

    override suspend fun logout() {
        ApiClient.setToken(null)
        session.clearSession()
    }

    override suspend fun restoreSession(): Boolean {
        val token = session.getToken()
        return if (token != null) {
            ApiClient.setToken(token)
            true
        } else {
            false
        }
    }

    private fun parseError(e: HttpException): String? =
        try { e.response()?.errorBody()?.string() } catch (_: Exception) { null }
}

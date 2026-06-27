package com.itba.homecore.data.repository

import android.content.Context
import com.itba.homecore.data.api.ApiClient
import com.itba.homecore.data.api.KtorApiException
import com.itba.homecore.data.api.KtorClient
import com.itba.homecore.data.api.ktorCall
import com.itba.homecore.data.api.unwrap
import com.itba.homecore.data.local.SessionManager
import com.itba.homecore.data.model.AuthResponse
import com.itba.homecore.data.model.ChangePasswordRequest
import com.itba.homecore.data.model.CodeRequest
import com.itba.homecore.data.model.EmailRequest
import com.itba.homecore.data.model.LoginRequest
import com.itba.homecore.data.model.RegisterRequest
import com.itba.homecore.data.model.ResetPasswordRequest
import com.itba.homecore.data.model.User
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody

/**
 * Real authentication against the HCI API (Ktor) plus DataStore session persistence.
 */
class RemoteAuthRepository(context: Context) : AuthRepository {
    private val http    = KtorClient.http
    private val session = SessionManager(context)

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        ktorCall("Email o contraseña incorrectos") {
            val auth  = http.post("users/login") { setBody(LoginRequest(email, password)) }.unwrap<AuthResponse>()
            val token = auth.token ?: throw Exception("No se recibió token")

            ApiClient.setToken(token)
            session.saveToken(token)

            val user = auth.user ?: User(email = email)
            session.saveUserInfo(user.id, user.fullName, user.email)
            user
        }
    }

    /**
     * Two-step registration: create the account, then trigger the verification code
     * email (/send-verification). A 409 means the email is already taken, surfaced as
     * [EmailAlreadyRegisteredException] so the UI can send the user to sign in.
     */
    override suspend fun register(name: String, lastName: String, email: String, password: String): Result<Unit> = runCatching {
        val fullName = if (lastName.isBlank()) name else "$name $lastName"
        try {
            http.post("users/register") { setBody(RegisterRequest(fullName.trim(), email.trim(), password)) }
        } catch (e: KtorApiException) {
            if (e.status == 409) {
                throw EmailAlreadyRegisteredException("Este email ya está registrado. Iniciá sesión.")
            }
            throw Exception(e.message ?: "No se pudo crear la cuenta")
        }
        sendVerification(email).getOrThrow()
    }

    override suspend fun logout() {
        // Only hit the network when a token is still held in memory. A logout triggered
        // by the 401 handler runs after it already cleared the token, so an unauthenticated
        // POST /users/logout would 401 again and re-emit in a loop.
        if (ApiClient.hasToken()) {
            runCatching { http.post("users/logout") }
        }
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

    override suspend fun verifyAccount(code: String): Result<Unit> = runCatching {
        ktorCall("El código no es válido o expiró") {
            http.post("users/verify-account") { setBody(CodeRequest(code.trim())) }
            Unit
        }
    }

    override suspend fun sendVerification(email: String): Result<Unit> = runCatching {
        ktorCall("No se pudo enviar el código") {
            http.post("users/send-verification") { setBody(EmailRequest(email.trim())) }
            Unit
        }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> = runCatching {
        ktorCall("No se pudo enviar el código de recuperación") {
            http.post("users/forgot-password") { setBody(EmailRequest(email.trim())) }
            Unit
        }
    }

    override suspend fun resetPassword(code: String, newPassword: String): Result<Unit> = runCatching {
        ktorCall("No se pudo restablecer la contraseña") {
            http.post("users/reset-password") { setBody(ResetPasswordRequest(code.trim(), newPassword)) }
            Unit
        }
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> = runCatching {
        ktorCall("No se pudo cambiar la contraseña") {
            http.post("users/change-password") { setBody(ChangePasswordRequest(oldPassword, newPassword)) }
            Unit
        }
    }

    override suspend fun getProfile(): Result<User> = runCatching {
        ktorCall("No se pudo obtener el perfil") { http.get("users/profile").unwrap<User>() }
    }

    override suspend fun getSessionUser(): User? {
        val name  = session.getUserName()
        val email = session.getUserEmail()
        return if (name.isNullOrBlank() && email.isNullOrBlank()) null
               else User(name = name.orEmpty(), email = email.orEmpty())
    }
}

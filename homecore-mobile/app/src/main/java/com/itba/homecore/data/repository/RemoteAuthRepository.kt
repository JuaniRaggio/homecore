package com.itba.homecore.data.repository

import android.content.Context
import com.itba.homecore.data.api.ApiClient
import com.itba.homecore.data.api.apiCall
import com.itba.homecore.data.local.SessionManager
import com.itba.homecore.data.model.ChangePasswordRequest
import com.itba.homecore.data.model.CodeRequest
import com.itba.homecore.data.model.EmailRequest
import com.itba.homecore.data.model.LoginRequest
import com.itba.homecore.data.model.RegisterRequest
import com.itba.homecore.data.model.ResetPasswordRequest
import com.itba.homecore.data.model.User

/**
 * Real authentication against the HCI API plus DataStore session persistence.
 */
class RemoteAuthRepository(context: Context) : AuthRepository {
    private val api     = ApiClient.authApi
    private val session = SessionManager(context)

    override suspend fun login(email: String, password: String): Result<User> = runCatching {
        apiCall("Email o contraseña incorrectos") {
            val auth  = api.login(LoginRequest(email, password))
            val token = auth.token ?: throw Exception("No se recibió token")

            ApiClient.setToken(token)
            session.saveToken(token)

            val user = auth.user ?: User(email = email)
            session.saveUserInfo(user.id, user.fullName, user.email)
            user
        }
    }

    override suspend fun register(name: String, lastName: String, email: String, password: String): Result<Unit> = runCatching {
        apiCall("No se pudo crear la cuenta") {
            // The API takes a single name field, same as the web registration form.
            val fullName = if (lastName.isBlank()) name else "$name $lastName"
            api.register(RegisterRequest(fullName.trim(), email.trim(), password))
        }
        Unit
    }

    override suspend fun logout() {
        // Best effort: invalidate the token server-side, but never block the local logout.
        runCatching { api.logout() }
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
        apiCall("El código no es válido o expiró") { api.verifyAccount(CodeRequest(code.trim())) }
    }

    override suspend fun sendVerification(email: String): Result<Unit> = runCatching {
        apiCall("No se pudo reenviar el código") { api.sendVerification(EmailRequest(email.trim())) }
    }

    override suspend fun forgotPassword(email: String): Result<Unit> = runCatching {
        apiCall("No se pudo enviar el código de recuperación") { api.forgotPassword(EmailRequest(email.trim())) }
    }

    override suspend fun resetPassword(code: String, newPassword: String): Result<Unit> = runCatching {
        apiCall("No se pudo restablecer la contraseña") {
            api.resetPassword(ResetPasswordRequest(code.trim(), newPassword))
        }
    }

    override suspend fun changePassword(oldPassword: String, newPassword: String): Result<Unit> = runCatching {
        apiCall("No se pudo cambiar la contraseña") {
            api.changePassword(ChangePasswordRequest(oldPassword, newPassword))
        }
    }

    override suspend fun getProfile(): Result<User> = runCatching {
        apiCall("No se pudo obtener el perfil") { api.getProfile() }
    }
}

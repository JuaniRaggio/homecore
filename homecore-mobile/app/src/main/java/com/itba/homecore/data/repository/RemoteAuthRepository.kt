package com.itba.homecore.data.repository

import android.content.Context
import com.itba.homecore.data.api.ApiClient
import com.itba.homecore.data.api.apiCall
import com.itba.homecore.data.api.friendlyMessage
import com.itba.homecore.data.local.SessionManager
import com.itba.homecore.data.model.ChangePasswordRequest
import com.itba.homecore.data.model.CodeRequest
import com.itba.homecore.data.model.EmailRequest
import com.itba.homecore.data.model.LoginRequest
import com.itba.homecore.data.model.RegisterRequest
import com.itba.homecore.data.model.ResetPasswordRequest
import com.itba.homecore.data.model.User
import retrofit2.HttpException

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

    /**
     * Two-step registration: create the account, then trigger the verification code
     * email (/send-verification). A 409 means the email is already taken, surfaced as
     * [EmailAlreadyRegisteredException] so the UI can send the user to sign in.
     */
    override suspend fun register(name: String, lastName: String, email: String, password: String): Result<Unit> = runCatching {
        val fullName = if (lastName.isBlank()) name else "$name $lastName"
        try {
            api.register(RegisterRequest(fullName.trim(), email.trim(), password))
        } catch (e: HttpException) {
            if (e.code() == 409) {
                throw EmailAlreadyRegisteredException("Este email ya está registrado. Iniciá sesión.")
            }
            throw Exception(e.friendlyMessage("No se pudo crear la cuenta"))
        }
        sendVerification(email).getOrThrow()
    }

    override suspend fun logout() {
        // Only hit the network when a token is still held in memory. A logout triggered
        // by the 401 handler runs after the interceptor already cleared it, so an
        // unauthenticated POST /users/logout would 401 again and re-emit in a loop.
        if (ApiClient.hasToken()) {
            runCatching { api.logout() }
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
        apiCall("El código no es válido o expiró") { api.verifyAccount(CodeRequest(code.trim())) }
    }

    override suspend fun sendVerification(email: String): Result<Unit> = runCatching {
        apiCall("No se pudo enviar el código") { api.sendVerification(EmailRequest(email.trim())) }
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

    override suspend fun getSessionUser(): User? {
        val name  = session.getUserName()
        val email = session.getUserEmail()
        return if (name.isNullOrBlank() && email.isNullOrBlank()) null
               else User(name = name.orEmpty(), email = email.orEmpty())
    }
}

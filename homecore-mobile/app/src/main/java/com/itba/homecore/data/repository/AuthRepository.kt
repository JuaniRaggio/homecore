package com.itba.homecore.data.repository

import android.content.Context
import com.itba.homecore.data.api.ApiClient
import com.itba.homecore.data.local.SessionManager
import com.itba.homecore.data.model.LoginRequest
import com.itba.homecore.data.model.RegisterRequest
import com.itba.homecore.data.model.User
import retrofit2.HttpException

class AuthRepository(context: Context) {
    private val api     = ApiClient.authApi
    private val session = SessionManager(context)

    suspend fun login(email: String, password: String): Result<User> = runCatching {
        try {
            val auth  = api.login(LoginRequest(email, password))
            val token = auth.token ?: throw Exception("No se recibió token")
            session.saveToken(token)
            val user = auth.user ?: User(email = email)
            session.saveUserInfo(user.id, user.fullName, user.email)
            user
        } catch (e: HttpException) {
            throw Exception(parseError(e) ?: "Email o contraseña incorrectos")
        }
    }

    suspend fun register(name: String, lastName: String, email: String, password: String): Result<Unit> = runCatching {
        try {
            val fullName = if (lastName.isBlank()) name else "$name $lastName"
            api.register(RegisterRequest(fullName, email, password))
        } catch (e: HttpException) {
            throw Exception(parseError(e) ?: "Error al registrar (${e.code()})")
        }
    }

    private fun parseError(e: HttpException): String? {
        return try {
            e.response()?.errorBody()?.string()
        } catch (_: Exception) { null }
    }
}

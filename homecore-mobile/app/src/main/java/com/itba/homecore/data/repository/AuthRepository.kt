package com.itba.homecore.data.repository

import android.content.Context
import com.itba.homecore.data.api.ApiClient
import com.itba.homecore.data.local.SessionManager
import com.itba.homecore.data.model.LoginRequest
import com.itba.homecore.data.model.RegisterRequest
import com.itba.homecore.data.model.User

class AuthRepository(context: Context) {
    private val api     = ApiClient.authApi
    private val session = SessionManager(context)

    suspend fun login(email: String, password: String): Result<User> = runCatching {
        val resp  = api.login(LoginRequest(email, password))
        val auth  = resp.result ?: throw Exception(resp.error?.description ?: "Login fallido")
        val token = auth.token  ?: throw Exception("No se recibió token")
        session.saveToken(token)
        val user = auth.user ?: User(email = email)
        session.saveUserInfo(user.id, user.fullName, user.email)
        user
    }

    suspend fun register(name: String, lastName: String, email: String, password: String): Result<Unit> = runCatching {
        val resp = api.register(RegisterRequest(name, lastName, email, password))
        if (resp.error != null) throw Exception(resp.error.description ?: "Error al registrar")
    }
}

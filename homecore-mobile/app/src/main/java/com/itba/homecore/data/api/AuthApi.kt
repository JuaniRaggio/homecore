package com.itba.homecore.data.api

import com.itba.homecore.data.model.ApiResponse
import com.itba.homecore.data.model.AuthResponse
import com.itba.homecore.data.model.LoginRequest
import com.itba.homecore.data.model.RegisterRequest
import com.itba.homecore.data.model.User
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthResponse>

    @POST("users/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<User>
}

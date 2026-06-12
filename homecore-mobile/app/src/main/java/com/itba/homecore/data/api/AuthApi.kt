package com.itba.homecore.data.api

import com.itba.homecore.data.model.AuthResponse
import com.itba.homecore.data.model.LoginRequest
import com.itba.homecore.data.model.RegisterRequest
import com.itba.homecore.data.model.SendVerificationRequest
import com.itba.homecore.data.model.User
import com.itba.homecore.data.model.VerifyAccountRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("users/register")
    suspend fun register(@Body request: RegisterRequest): User

    /** Envía (o reenvía) el código de verificación al email de la cuenta. */
    @POST("users/send-verification")
    suspend fun sendVerification(@Body request: SendVerificationRequest)

    @POST("users/verify-account")
    suspend fun verifyAccount(@Body request: VerifyAccountRequest)
}

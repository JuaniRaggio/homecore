package com.itba.homecore.data.api

import com.itba.homecore.data.model.AuthResponse
import com.itba.homecore.data.model.ChangePasswordRequest
import com.itba.homecore.data.model.CodeRequest
import com.itba.homecore.data.model.EmailRequest
import com.itba.homecore.data.model.LoginRequest
import com.itba.homecore.data.model.RegisterRequest
import com.itba.homecore.data.model.ResetPasswordRequest
import com.itba.homecore.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {
    @POST("users/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("users/register")
    suspend fun register(@Body request: RegisterRequest): User

    @POST("users/logout")
    suspend fun logout()

    @POST("users/verify-account")
    suspend fun verifyAccount(@Body request: CodeRequest)

    /** Sends (or resends) the verification code to the account email. */
    @POST("users/send-verification")
    suspend fun sendVerification(@Body request: EmailRequest)

    @POST("users/forgot-password")
    suspend fun forgotPassword(@Body request: EmailRequest)

    @POST("users/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequest)

    @POST("users/change-password")
    suspend fun changePassword(@Body request: ChangePasswordRequest)

    @GET("users/profile")
    suspend fun getProfile(): User
}

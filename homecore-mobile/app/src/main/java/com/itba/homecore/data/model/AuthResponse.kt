package com.itba.homecore.data.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("token") val token: String?,
    @SerializedName("user")  val user: User?
)

data class LoginRequest(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("name")     val name: String,
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("metadata") val metadata: Map<String, Any> = emptyMap()
)

data class VerifyAccountRequest(
    @SerializedName("code") val code: String
)

data class SendVerificationRequest(
    @SerializedName("email") val email: String
)

data class CodeRequest(
    @SerializedName("code") val code: String
)

data class EmailRequest(
    @SerializedName("email") val email: String
)

data class ResetPasswordRequest(
    @SerializedName("code")     val code: String,
    @SerializedName("password") val password: String
)

data class ChangePasswordRequest(
    @SerializedName("oldPassword") val oldPassword: String,
    @SerializedName("newPassword") val newPassword: String
)

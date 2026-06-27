package com.itba.homecore.data.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    val token: String? = null,
    val user: User? = null
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String
)

@Serializable
data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

// Single request body per shape, reused across endpoints (verify-account uses {code};
// send-verification and forgot-password use {email}).
@Serializable
data class CodeRequest(
    val code: String
)

@Serializable
data class EmailRequest(
    val email: String
)

@Serializable
data class ResetPasswordRequest(
    val code: String,
    val password: String
)

@Serializable
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String
)

package com.itba.homecore.data.model

import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("result") val result: T?,
    @SerializedName("error") val error: ApiError?
)

data class ApiError(
    @SerializedName("code")        val code: Int?,
    @SerializedName("description") val description: String?
)

data class LoginRequest(
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("name")     val name: String,
    @SerializedName("lastName") val lastName: String,
    @SerializedName("email")    val email: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("token") val token: String?,
    @SerializedName("user")  val user: User?
)

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
    @SerializedName("password") val password: String
)

package com.itba.homecore.data.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val name: String = "",
    val lastName: String? = null,
    val email: String = ""
) {
    val fullName: String get() = if (!lastName.isNullOrBlank()) "$name $lastName" else name
}

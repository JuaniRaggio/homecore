package com.itba.homecore.data.model

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id") val id: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("lastName") val lastName: String? = null,
    @SerializedName("email") val email: String = ""
) {
    val fullName: String get() = if (!lastName.isNullOrBlank()) "$name $lastName" else name
}

package com.itba.homecore.data.model

import com.google.gson.annotations.SerializedName

/**
 * Wrapper genérico que devuelve la API HCI cuando hay error.
 * Para respuestas de éxito devuelve el objeto directo (sin wrapper).
 */
data class ApiResponse<T>(
    @SerializedName("result") val result: T?,
    @SerializedName("error")  val error: ApiError?
)

data class ApiError(
    @SerializedName("code")        val code: Int?,
    @SerializedName("description") val description: String?
)

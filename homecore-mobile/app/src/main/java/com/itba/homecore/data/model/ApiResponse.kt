package com.itba.homecore.data.model

import com.google.gson.annotations.SerializedName

/**
 * Generic wrapper the HCI API returns on error.
 * For success responses it returns the object directly (no wrapper).
 */
data class ApiResponse<T>(
    @SerializedName("result") val result: T?,
    @SerializedName("error")  val error: ApiError?
)

data class ApiError(
    @SerializedName("code")        val code: Int?,
    @SerializedName("description") val description: String?
)

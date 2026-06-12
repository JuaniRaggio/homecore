package com.itba.homecore.data.api

import com.google.gson.Gson
import com.itba.homecore.data.model.ApiResponse
import retrofit2.HttpException
import java.io.IOException

/**
 * Single place to translate network errors into user-facing messages, so the
 * Remote* repositories do not duplicate the parsing (second delivery feedback
 * about repeated helpers and generic error messages).
 */

/** Extracts `error.description` from the API error body { "error": { "code", "description" } }. */
fun HttpException.friendlyMessage(fallback: String): String = try {
    val raw = response()?.errorBody()?.string()
    if (raw.isNullOrBlank()) fallback
    else Gson().fromJson(raw, ApiResponse::class.java)?.error?.description ?: fallback
} catch (_: Exception) {
    fallback
}

/**
 * Runs an API call converting failures into exceptions with a user-facing message:
 * HTTP errors use the API description (or [fallback]) and connectivity errors get
 * a specific network message.
 */
suspend fun <T> apiCall(fallback: String, block: suspend () -> T): T = try {
    block()
} catch (e: HttpException) {
    throw Exception(e.friendlyMessage(fallback))
} catch (e: IOException) {
    throw Exception("No se pudo conectar. Verificá tu conexión a internet.")
}

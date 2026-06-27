package com.itba.homecore.data.api

import com.itba.homecore.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.HttpResponseValidator
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

/**
 * Ktor-based HTTP client backing every repository. Uses the pure-Kotlin CIO engine
 * (no OkHttp / no Java HTTP stack) and kotlinx.serialization for JSON.
 *
 * Responsibilities:
 *  - Injects `X-API-Key` and the `Authorization: Bearer <token>` header.
 *  - Reads the token from [ApiClient] (the in-memory session token holder).
 *  - Unwraps the `{ "result": ... }` envelope via [unwrap].
 *  - Maps 401 responses to [SessionEvents.emitUnauthorized] when there was a token.
 *  - Surfaces the API's `error.description` (and the HTTP status) as [KtorApiException].
 */
object KtorClient {

    val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        explicitNulls = false
    }

    val http: HttpClient = HttpClient(CIO) {
        expectSuccess = true

        install(ContentNegotiation) {
            json(json)
        }

        install(Logging) {
            level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.NONE
        }

        defaultRequest {
            url(BuildConfig.API_BASE_URL)
            header("X-API-Key", BuildConfig.API_KEY)
            ApiClient.currentToken()?.let { token ->
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            contentType(ContentType.Application.Json)
        }

        HttpResponseValidator {
            handleResponseExceptionWithRequest { cause, _ ->
                if (cause !is ClientRequestException) return@handleResponseExceptionWithRequest

                val response = cause.response
                val status = response.status.value
                if (status == 401 && ApiClient.hasToken()) {
                    ApiClient.clearToken()
                    SessionEvents.emitUnauthorized()
                }

                val description = runCatching {
                    val text = response.bodyAsText()
                    if (text.isBlank()) null
                    else json.parseToJsonElement(text)
                        .jsonObject["error"]
                        ?.jsonObject?.get("description")
                        ?.toString()
                        ?.trim('"')
                }.getOrNull()

                throw KtorApiException(description ?: "Error $status", status, cause)
            }
        }
    }
}

/** Exception thrown by [KtorClient] for a non-2xx response, carrying the HTTP [status] and description. */
class KtorApiException(message: String, val status: Int, cause: Throwable? = null) : Exception(message, cause)

/** Decodes the response body and returns the inner `result` (or the whole object if there is no envelope). */
suspend inline fun <reified T> HttpResponse.unwrap(): T {
    val text = bodyAsText()
    val element = KtorClient.json.parseToJsonElement(text)
    val obj = element as? JsonObject
    val result = obj?.get("result") ?: element
    return KtorClient.json.decodeFromJsonElement(kotlinx.serialization.serializer<T>(), result)
}

/**
 * Runs a Ktor block translating exceptions into a single user-facing message,
 * so the repositories do not duplicate the parsing.
 */
suspend fun <T> ktorCall(fallback: String, block: suspend () -> T): T = try {
    block()
} catch (e: KtorApiException) {
    throw Exception(e.message ?: fallback)
} catch (_: java.io.IOException) {
    throw Exception("No se pudo conectar. Verificá tu conexión a internet.")
} catch (e: Exception) {
    throw Exception(fallback, e)
}

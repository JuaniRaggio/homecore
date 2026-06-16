package com.itba.homecore.data.api

import com.itba.homecore.BuildConfig
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.OkHttp
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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

/**
 * Ktor-based HTTP client, used by [com.itba.homecore.data.repository.RemoteRoutinesRepository]
 * as a proof of concept of the Ktor pattern shown in class (`YesOrNoApi`).
 *
 * Mirrors [ApiClient] in its responsibilities:
 *  - Injects `X-API-Key` and the `Authorization: Bearer <token>` header.
 *  - Reads the token from [ApiClient] so the Retrofit and Ktor clients share the same session.
 *  - Unwraps the `{ "result": ... }` envelope via [unwrap].
 *  - Maps 401 responses to [SessionEvents.emitUnauthorized] when there was a token.
 *  - Surfaces the API's `error.description` as the exception message.
 */
object KtorClient {

    val json: Json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        explicitNulls = false
    }

    val http: HttpClient = HttpClient(OkHttp) {
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
                val hadToken = ApiClient.hasToken()
                if (response.status.value == 401 && hadToken) {
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

                if (!description.isNullOrBlank()) throw KtorApiException(description, cause)
            }
        }
    }
}

/** Exception thrown by [KtorClient] when the API responds with a non-2xx status with a description. */
class KtorApiException(message: String, cause: Throwable? = null) : Exception(message, cause)

/** Envelope for `{ "result": T }` success responses returned by the HCI API. */
@Serializable
data class KtorEnvelope<T>(val result: T? = null)

/** Decodes the response body and returns the inner `result`. */
suspend inline fun <reified T> HttpResponse.unwrap(): T {
    val text = bodyAsText()
    val element = KtorClient.json.parseToJsonElement(text)
    val obj = element as? JsonObject
    val result = obj?.get("result") ?: element
    return KtorClient.json.decodeFromJsonElement(kotlinx.serialization.serializer<T>(), result)
}

/**
 * Runs a Ktor block translating exceptions into a single user-facing message,
 * mirroring [apiCall] used by the Retrofit repositories.
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

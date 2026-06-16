package com.itba.homecore.data.repository

import com.itba.homecore.data.api.KtorClient
import com.itba.homecore.data.api.ktorCall
import com.itba.homecore.data.api.unwrap
import com.itba.homecore.data.model.Routine
import com.itba.homecore.data.model.RoutineMetadata
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonArray
import kotlinx.serialization.json.buildJsonObject

/**
 * Ktor-based implementation of [RoutinesRepository]. Used as the proof-of-concept of the
 * Ktor pattern shown in class ([Ktor/.../YesOrNoApi.kt]); the rest of the data layer still
 * uses Retrofit. The interface contract is unchanged, so ViewModels do not care which
 * client backs the calls.
 */
class RemoteRoutinesRepository : RoutinesRepository {

    private val http = KtorClient.http

    override suspend fun getRoutines(): Result<List<Routine>> = runCatching {
        ktorCall("Error al obtener rutinas") {
            http.get("routines").unwrap<List<Routine>>()
        }
    }

    override suspend fun getRoutine(id: String): Result<Routine> = runCatching {
        ktorCall("No se pudo obtener la rutina") {
            http.get("routines/$id").unwrap<Routine>()
        }
    }

    override suspend fun createRoutine(routine: Routine): Result<Routine> = runCatching {
        ktorCall("No se pudo crear la rutina") {
            http.post("routines") { setBody(fullBody(routine)) }.unwrap<Routine>()
        }
    }

    override suspend fun saveRoutine(routine: Routine): Result<Routine> = runCatching {
        ktorCall("No se pudo guardar la rutina") {
            http.put("routines/${routine.id}") { setBody(fullBody(routine)) }.unwrap<Routine>()
        }
    }

    override suspend fun executeRoutine(id: String): Result<Unit> = runCatching {
        ktorCall("No se pudo ejecutar la rutina") {
            http.patch("routines/$id/execute")
            Unit
        }
    }

    override suspend fun toggleFavorite(routine: Routine): Result<Routine> = runCatching {
        val newFavorite = routine.metadata?.favorite != true
        ktorCall("No se pudo marcar como favorita") {
            http.put("routines/${routine.id}") {
                setBody(buildBody(routine, favorite = newFavorite))
            }.unwrap<Routine>()
        }
    }

    override suspend fun toggleActive(routine: Routine): Result<Routine> = runCatching {
        val newActive = !(routine.metadata?.active ?: true)
        ktorCall("No se pudo cambiar el estado") {
            http.put("routines/${routine.id}") {
                setBody(buildBody(routine, active = newActive))
            }.unwrap<Routine>()
        }
    }

    override suspend fun deleteRoutine(id: String): Result<Unit> = runCatching {
        ktorCall("No se pudo eliminar la rutina") {
            http.delete("routines/$id")
            Unit
        }
    }

    /** PUT body for a single toggled metadata flag (favorite/active). */
    private fun buildBody(
        routine: Routine,
        favorite: Boolean = routine.metadata?.favorite ?: false,
        active: Boolean = routine.metadata?.active ?: true
    ): JsonObject = fullBody(
        routine.copy(
            metadata = (routine.metadata ?: RoutineMetadata()).copy(favorite = favorite, active = active)
        )
    )

    /**
     * Full routine body for POST/PUT. Same shape as the previous Retrofit version:
     * top-level name/description/actions/time/days plus metadata. Actions reduced to
     * { device:{id}, actionName, params } so the API can resolve the device by id.
     */
    private fun fullBody(routine: Routine): JsonObject {
        val description = routine.description ?: routine.metadata?.description
        val time = routine.metadata?.time
        val days = routine.metadata?.days
        return buildJsonObject {
            put("name", JsonPrimitive(routine.name))
            put("description", description?.let(::JsonPrimitive) ?: JsonNull)
            put("actions", buildJsonArray {
                routine.actions.forEach { a ->
                    add(buildJsonObject {
                        put("device", buildJsonObject { put("id", JsonPrimitive(a.device?.id ?: "")) })
                        put("actionName", JsonPrimitive(a.actionName))
                        put("params", buildJsonArray { a.params.forEach { add(anyToJson(it)) } })
                    })
                }
            })
            put("time", time?.let(::JsonPrimitive) ?: JsonNull)
            put("days", days?.let { buildJsonArray { it.forEach { d -> add(JsonPrimitive(d)) } } } ?: JsonNull)
            put("metadata", buildJsonObject {
                put("favorite", JsonPrimitive(routine.metadata?.favorite ?: false))
                put("active",   JsonPrimitive(routine.metadata?.active ?: true))
                put("time",     time?.let(::JsonPrimitive) ?: JsonNull)
                put("days",     days?.let { buildJsonArray { it.forEach { d -> add(JsonPrimitive(d)) } } } ?: JsonNull)
                put("description", description?.let(::JsonPrimitive) ?: JsonNull)
                put("homeId",   routine.metadata?.homeId?.let(::JsonPrimitive) ?: JsonNull)
            })
        }
    }

    private fun anyToJson(v: Any?): JsonElement = when (v) {
        null       -> JsonNull
        is Boolean -> JsonPrimitive(v)
        is Number  -> JsonPrimitive(v)
        is String  -> JsonPrimitive(v)
        else       -> JsonPrimitive(v.toString())
    }
}

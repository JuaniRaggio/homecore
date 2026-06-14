package com.itba.homecore.data.repository

import com.itba.homecore.data.api.ApiClient
import com.itba.homecore.data.api.apiCall
import com.itba.homecore.data.model.Routine
import com.itba.homecore.data.model.RoutineMetadata

/**
 * Real implementation against the HCI API (Retrofit).
 */
class RemoteRoutinesRepository : RoutinesRepository {
    private val routinesApi = ApiClient.routinesApi

    override suspend fun getRoutines(): Result<List<Routine>> = runCatching {
        apiCall("Error al obtener rutinas") { routinesApi.getAllRoutines() }
    }

    override suspend fun getRoutine(id: String): Result<Routine> = runCatching {
        apiCall("No se pudo obtener la rutina") { routinesApi.getRoutine(id) }
    }

    override suspend fun createRoutine(routine: Routine): Result<Routine> = runCatching {
        apiCall("No se pudo crear la rutina") { routinesApi.createRoutine(fullBody(routine)) }
    }

    override suspend fun saveRoutine(routine: Routine): Result<Routine> = runCatching {
        apiCall("No se pudo guardar la rutina") { routinesApi.updateRoutine(routine.id, fullBody(routine)) }
    }

    override suspend fun executeRoutine(id: String): Result<Unit> = runCatching {
        apiCall("No se pudo ejecutar la rutina") { routinesApi.executeRoutine(id).close() }
    }

    override suspend fun toggleFavorite(routine: Routine): Result<Routine> = runCatching {
        val newFavorite = routine.metadata?.favorite != true
        apiCall("No se pudo marcar como favorita") {
            routinesApi.updateRoutine(routine.id, buildBody(routine, favorite = newFavorite))
        }
    }

    override suspend fun toggleActive(routine: Routine): Result<Routine> = runCatching {
        val newActive = !(routine.metadata?.active ?: true)
        apiCall("No se pudo cambiar el estado") {
            routinesApi.updateRoutine(routine.id, buildBody(routine, active = newActive))
        }
    }

    override suspend fun deleteRoutine(id: String): Result<Unit> = runCatching {
        apiCall("No se pudo eliminar la rutina") { routinesApi.deleteRoutine(id) }
    }

    /** PUT body for a single toggled metadata flag (favorite/active). */
    private fun buildBody(
        routine: Routine,
        favorite: Boolean = routine.metadata?.favorite ?: false,
        active: Boolean = routine.metadata?.active ?: true
    ): Map<String, Any?> = fullBody(
        routine.copy(
            metadata = (routine.metadata ?: RoutineMetadata()).copy(favorite = favorite, active = active)
        )
    )

    /**
     * Full routine body for POST/PUT. Actions are reduced to the shape the API expects
     * ({ device: { id }, actionName, params }) rather than the full embedded device.
     */
    private fun fullBody(routine: Routine): Map<String, Any?> {
        val meta = mapOf(
            "favorite"    to (routine.metadata?.favorite ?: false),
            "active"      to (routine.metadata?.active ?: true),
            "time"        to routine.metadata?.time,
            "days"        to routine.metadata?.days,
            "description" to (routine.description ?: routine.metadata?.description),
            "homeId"      to routine.metadata?.homeId
        )
        val actions = routine.actions.map { a ->
            mapOf(
                "device"     to mapOf("id" to (a.device?.id ?: "")),
                "actionName" to a.actionName,
                "params"     to a.params
            )
        }
        return mapOf(
            "name"     to routine.name,
            "actions"  to actions,
            "metadata" to meta
        )
    }
}

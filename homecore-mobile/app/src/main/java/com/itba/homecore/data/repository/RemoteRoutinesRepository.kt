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
     * Full routine body for POST/PUT: top-level name/description/actions/time/days plus
     * metadata. The HCI API reads schedule from the top level (not metadata), so missing
     * them there leaves the routine unschedulable. Actions reduced to { device:{id}, actionName, params }.
     */
    private fun fullBody(routine: Routine): Map<String, Any?> {
        val description = routine.description ?: routine.metadata?.description
        val time = routine.metadata?.time
        val days = routine.metadata?.days
        val meta = mapOf(
            "favorite"    to (routine.metadata?.favorite ?: false),
            "active"      to (routine.metadata?.active ?: true),
            "time"        to time,
            "days"        to days,
            "description" to description,
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
            "name"        to routine.name,
            "description" to description,
            "actions"     to actions,
            "time"        to time,
            "days"        to days,
            "metadata"    to meta
        )
    }
}

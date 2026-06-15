package com.itba.homecore.data.repository

import com.itba.homecore.data.api.ApiClient
import com.itba.homecore.data.api.apiCall
import com.itba.homecore.data.model.Routine

/**
 * Real implementation against the HCI API (Retrofit).
 */
class RemoteRoutinesRepository : RoutinesRepository {
    private val routinesApi = ApiClient.routinesApi

    override suspend fun getRoutines(): Result<List<Routine>> = runCatching {
        apiCall("Error al obtener rutinas") { routinesApi.getAllRoutines() }
    }

    override suspend fun getRoutineById(id: String): Result<Routine> = runCatching {
        apiCall("Error al obtener la rutina") { routinesApi.getRoutine(id) }
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

    /** PUT requires the full routine body: only the toggled metadata flag changes. */
    private fun buildBody(
        routine: Routine,
        favorite: Boolean = routine.metadata?.favorite ?: false,
        active: Boolean = routine.metadata?.active ?: true
    ): Map<String, Any?> {
        val meta = mapOf(
            "favorite"    to favorite,
            "active"      to active,
            "time"        to routine.metadata?.time,
            "days"        to routine.metadata?.days,
            "description" to routine.metadata?.description,
            "homeId"      to routine.metadata?.homeId
        )
        return mapOf(
            "name"     to routine.name,
            "actions"  to routine.actions,
            "metadata" to meta
        )
    }
}

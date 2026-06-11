package com.itba.homecore.data.repository

import com.itba.homecore.data.api.ApiClient
import com.itba.homecore.data.model.Routine
import retrofit2.HttpException

class RoutinesRepository {
    private val routinesApi = ApiClient.routinesApi

    suspend fun getRoutines(): Result<List<Routine>> = runCatching {
        try { routinesApi.getAllRoutines() }
        catch (e: HttpException) { throw Exception(httpMsg(e, "Error al obtener rutinas")) }
    }

    suspend fun executeRoutine(id: String): Result<Unit> = runCatching {
        try { routinesApi.executeRoutine(id); Unit }
        catch (e: HttpException) { throw Exception(httpMsg(e, "No se pudo ejecutar la rutina")) }
    }

    suspend fun toggleFavorite(routine: Routine): Result<Routine> = runCatching {
        val newFavorite = routine.metadata?.favorite != true
        val newMeta = mapOf(
            "favorite"    to newFavorite,
            "active"      to (routine.metadata?.active ?: true),
            "time"        to routine.metadata?.time,
            "days"        to routine.metadata?.days,
            "description" to routine.metadata?.description,
            "homeId"      to routine.metadata?.homeId
        )
        val body = mapOf(
            "name"     to routine.name,
            "actions"  to routine.actions,
            "metadata" to newMeta
        )
        try { routinesApi.updateRoutine(routine.id, body) }
        catch (e: HttpException) { throw Exception(httpMsg(e, "No se pudo marcar como favorita")) }
    }

    suspend fun toggleActive(routine: Routine): Result<Routine> = runCatching {
        val newActive = !(routine.metadata?.active ?: true)
        val newMeta = mapOf(
            "favorite"    to (routine.metadata?.favorite ?: false),
            "active"      to newActive,
            "time"        to routine.metadata?.time,
            "days"        to routine.metadata?.days,
            "description" to routine.metadata?.description,
            "homeId"      to routine.metadata?.homeId
        )
        val body = mapOf(
            "name"     to routine.name,
            "actions"  to routine.actions,
            "metadata" to newMeta
        )
        try { routinesApi.updateRoutine(routine.id, body) }
        catch (e: HttpException) { throw Exception(httpMsg(e, "No se pudo cambiar el estado")) }
    }

    private fun httpMsg(e: HttpException, fallback: String) =
        try { e.response()?.errorBody()?.string() ?: fallback } catch (_: Exception) { fallback }
}

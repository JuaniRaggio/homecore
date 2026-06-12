package com.itba.homecore.data.repository

import com.itba.homecore.data.api.ApiClient
import com.itba.homecore.data.model.Routine
import retrofit2.HttpException

/**
 * Implementación real contra la API HCI (Retrofit).
 */
class RemoteRoutinesRepository : RoutinesRepository {
    private val routinesApi = ApiClient.routinesApi

    override suspend fun getRoutines(): Result<List<Routine>> = runCatching {
        try { routinesApi.getAllRoutines() }
        catch (e: HttpException) { throw Exception(httpMsg(e, "Error al obtener rutinas")) }
    }

    override suspend fun executeRoutine(id: String): Result<Unit> = runCatching {
        try { routinesApi.executeRoutine(id); Unit }
        catch (e: HttpException) { throw Exception(httpMsg(e, "No se pudo ejecutar la rutina")) }
    }

    override suspend fun toggleFavorite(routine: Routine): Result<Routine> = runCatching {
        val newFavorite = routine.metadata?.favorite != true
        try { routinesApi.updateRoutine(routine.id, buildBody(routine, favorite = newFavorite)) }
        catch (e: HttpException) { throw Exception(httpMsg(e, "No se pudo marcar como favorita")) }
    }

    override suspend fun toggleActive(routine: Routine): Result<Routine> = runCatching {
        val newActive = !(routine.metadata?.active ?: true)
        try { routinesApi.updateRoutine(routine.id, buildBody(routine, active = newActive)) }
        catch (e: HttpException) { throw Exception(httpMsg(e, "No se pudo cambiar el estado")) }
    }

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

    private fun httpMsg(e: HttpException, fallback: String) =
        try { e.response()?.errorBody()?.string() ?: fallback } catch (_: Exception) { fallback }
}

package com.itba.homecore.data.repository

import com.itba.homecore.data.model.Routine

/**
 * Data-layer contract for routines. See [DevicesRepository] for the pattern.
 */
interface RoutinesRepository {
    suspend fun getRoutines(): Result<List<Routine>>
    suspend fun executeRoutine(id: String): Result<Unit>
    suspend fun toggleFavorite(routine: Routine): Result<Routine>
    suspend fun toggleActive(routine: Routine): Result<Routine>
    suspend fun deleteRoutine(id: String): Result<Unit>
}

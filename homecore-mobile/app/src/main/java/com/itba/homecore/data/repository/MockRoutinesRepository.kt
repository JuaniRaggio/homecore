package com.itba.homecore.data.repository

import com.itba.homecore.data.mock.MockData
import com.itba.homecore.data.model.Routine
import com.itba.homecore.data.model.RoutineMetadata
import kotlinx.coroutines.delay

/**
 * Prototype implementation of routines on top of [MockData].
 */
class MockRoutinesRepository : RoutinesRepository {

    override suspend fun getRoutines(): Result<List<Routine>> = runCatching {
        delay(300)
        MockData.routines.toList()
    }

    override suspend fun executeRoutine(id: String): Result<Unit> = runCatching {
        delay(400) // Simulates the sequential execution of the actions.
    }

    override suspend fun toggleFavorite(routine: Routine): Result<Routine> = runCatching {
        val idx = MockData.routines.indexOfFirst { it.id == routine.id }
        require(idx >= 0) { "Rutina no encontrada" }
        val current = MockData.routines[idx]
        val newMeta = (current.metadata ?: RoutineMetadata())
            .copy(favorite = !(current.metadata?.favorite ?: false))
        val updated = current.copy(metadata = newMeta)
        MockData.routines[idx] = updated
        updated
    }

    override suspend fun toggleActive(routine: Routine): Result<Routine> = runCatching {
        val idx = MockData.routines.indexOfFirst { it.id == routine.id }
        require(idx >= 0) { "Rutina no encontrada" }
        val current = MockData.routines[idx]
        val newMeta = (current.metadata ?: RoutineMetadata())
            .copy(active = !(current.metadata?.active ?: true))
        val updated = current.copy(metadata = newMeta)
        MockData.routines[idx] = updated
        updated
    }

    override suspend fun deleteRoutine(id: String): Result<Unit> = runCatching {
        delay(200)
        MockData.routines.removeAll { it.id == id }
        Unit
    }
}

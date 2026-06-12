package com.itba.homecore.data.repository

import com.itba.homecore.data.mock.MockData
import com.itba.homecore.data.model.Routine
import kotlinx.coroutines.delay

/**
 * Implementación de prototipo de rutinas sobre [MockData].
 */
class MockRoutinesRepository : RoutinesRepository {

    override suspend fun getRoutines(): Result<List<Routine>> = runCatching {
        delay(300)
        MockData.routines.toList()
    }

    override suspend fun executeRoutine(id: String): Result<Unit> = runCatching {
        delay(400) // Simula la ejecución secuencial de las acciones.
    }

    override suspend fun toggleFavorite(routine: Routine): Result<Routine> = runCatching {
        val idx = MockData.routines.indexOfFirst { it.id == routine.id }
        require(idx >= 0) { "Rutina no encontrada" }
        val current = MockData.routines[idx]
        val newMeta = (current.metadata ?: com.itba.homecore.data.model.RoutineMetadata())
            .copy(favorite = !(current.metadata?.favorite ?: false))
        val updated = current.copy(metadata = newMeta)
        MockData.routines[idx] = updated
        updated
    }

    override suspend fun toggleActive(routine: Routine): Result<Routine> = runCatching {
        val idx = MockData.routines.indexOfFirst { it.id == routine.id }
        require(idx >= 0) { "Rutina no encontrada" }
        val current = MockData.routines[idx]
        val newMeta = (current.metadata ?: com.itba.homecore.data.model.RoutineMetadata())
            .copy(active = !(current.metadata?.active ?: true))
        val updated = current.copy(metadata = newMeta)
        MockData.routines[idx] = updated
        updated
    }
}

package com.itba.homecore.data.repository

import com.itba.homecore.data.model.Routine

/**
 * Contrato de la capa de datos de rutinas. Ver [DevicesRepository] para el patrón.
 */
interface RoutinesRepository {
    suspend fun getRoutines(): Result<List<Routine>>
    suspend fun executeRoutine(id: String): Result<Unit>
    suspend fun toggleFavorite(routine: Routine): Result<Routine>
    suspend fun toggleActive(routine: Routine): Result<Routine>
}

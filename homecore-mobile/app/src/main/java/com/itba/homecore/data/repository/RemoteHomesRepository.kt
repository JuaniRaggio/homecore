package com.itba.homecore.data.repository

import com.itba.homecore.data.api.ApiClient
import com.itba.homecore.data.api.apiCall
import com.itba.homecore.data.model.Home

/**
 * Homes implementation against the HCI API (Retrofit), wired in [com.itba.homecore.di.AppModule].
 */
class RemoteHomesRepository : HomesRepository {
    private val api = ApiClient.homesApi

    override suspend fun getHomes(): Result<List<Home>> = runCatching {
        apiCall("Error al obtener hogares") { api.getAllHomes() }
    }

    override suspend fun createHome(name: String): Result<Home> = runCatching {
        apiCall("No se pudo crear el hogar") { api.createHome(mapOf("name" to name.trim())) }
    }

    override suspend fun renameHome(id: String, newName: String): Result<Home> = runCatching {
        apiCall("No se pudo renombrar el hogar") { api.updateHome(id, mapOf("name" to newName.trim())) }
    }

    override suspend fun deleteHome(id: String): Result<Unit> = runCatching {
        apiCall("No se pudo eliminar el hogar") { api.deleteHome(id) }
    }
}

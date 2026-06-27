package com.itba.homecore.data.repository

import com.itba.homecore.data.api.KtorClient
import com.itba.homecore.data.api.ktorCall
import com.itba.homecore.data.api.unwrap
import com.itba.homecore.data.model.Home
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

/**
 * Homes implementation against the HCI API (Ktor), wired in [com.itba.homecore.di.AppModule].
 */
class RemoteHomesRepository : HomesRepository {
    private val http = KtorClient.http

    override suspend fun getHomes(): Result<List<Home>> = runCatching {
        ktorCall("Error al obtener hogares") { http.get("homes").unwrap<List<Home>>() }
    }

    override suspend fun createHome(name: String): Result<Home> = runCatching {
        ktorCall("No se pudo crear el hogar") {
            http.post("homes") { setBody(buildJsonObject { put("name", name.trim()) }) }.unwrap<Home>()
        }
    }

    override suspend fun renameHome(id: String, newName: String): Result<Home> = runCatching {
        ktorCall("No se pudo renombrar el hogar") {
            http.put("homes/$id") { setBody(buildJsonObject { put("name", newName.trim()) }) }.unwrap<Home>()
        }
    }

    override suspend fun deleteHome(id: String): Result<Unit> = runCatching {
        ktorCall("No se pudo eliminar el hogar") { http.delete("homes/$id"); Unit }
    }
}

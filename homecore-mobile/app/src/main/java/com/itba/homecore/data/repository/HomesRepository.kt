package com.itba.homecore.data.repository

import com.itba.homecore.data.model.Home

/**
 * Data-layer contract for homes (RF17-RF19). Implemented by [RemoteHomesRepository]
 * against the HCI API and wired in [com.itba.homecore.di.AppModule].
 */
interface HomesRepository {
    suspend fun getHomes(): Result<List<Home>>
    suspend fun createHome(name: String): Result<Home>
    suspend fun renameHome(id: String, newName: String): Result<Home>
    suspend fun deleteHome(id: String): Result<Unit>
}

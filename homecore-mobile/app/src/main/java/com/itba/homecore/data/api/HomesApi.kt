package com.itba.homecore.data.api

import com.itba.homecore.data.model.Home
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

/**
 * Homes endpoints (optional). Methods are defined because POST /rooms
 * accepts a home reference and the feed header needs the real home name.
 */
interface HomesApi {
    @GET("homes")
    suspend fun getAllHomes(): List<Home>

    @GET("homes/{id}")
    suspend fun getHome(@Path("id") id: String): Home

    @POST("homes")
    suspend fun createHome(@Body body: Map<String, @JvmSuppressWildcards Any?>): Home

    @PUT("homes/{id}")
    suspend fun updateHome(
        @Path("id") id: String,
        @Body body: Map<String, @JvmSuppressWildcards Any?>
    ): Home

    @DELETE("homes/{id}")
    suspend fun deleteHome(@Path("id") id: String)
}

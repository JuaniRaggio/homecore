package com.itba.homecore.data.api

import com.itba.homecore.data.model.Routine
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.PUT
import retrofit2.http.Path

interface RoutinesApi {
    @GET("routines")
    suspend fun getAllRoutines(): List<Routine>

    @GET("routines/{id}")
    suspend fun getRoutine(@Path("id") id: String): Routine

    @PATCH("routines/{id}/execute")
    suspend fun executeRoutine(@Path("id") id: String): Boolean

    @PUT("routines/{id}")
    suspend fun updateRoutine(
        @Path("id") id: String,
        @Body body: Map<String, @JvmSuppressWildcards Any?>
    ): Routine
}

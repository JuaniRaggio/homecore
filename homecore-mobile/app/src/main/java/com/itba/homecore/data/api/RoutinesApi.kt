package com.itba.homecore.data.api

import com.itba.homecore.data.model.Routine
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RoutinesApi {
    @GET("routines")
    suspend fun getAllRoutines(): List<Routine>

    @GET("routines/{id}")
    suspend fun getRoutine(@Path("id") id: String): Routine

    @POST("routines")
    suspend fun createRoutine(@Body body: Map<String, @JvmSuppressWildcards Any?>): Routine

    @DELETE("routines/{id}")
    suspend fun deleteRoutine(@Path("id") id: String)

    @PATCH("routines/{id}/execute")
    suspend fun executeRoutine(@Path("id") id: String): Boolean

    @PUT("routines/{id}")
    suspend fun updateRoutine(
        @Path("id") id: String,
        @Body body: Map<String, @JvmSuppressWildcards Any?>
    ): Routine
}

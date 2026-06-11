package com.itba.homecore.data.api

import com.itba.homecore.data.model.Device
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Path

interface DevicesApi {
    @GET("devices")
    suspend fun getAllDevices(): List<Device>

    @PATCH("devices/{id}/{action}")
    suspend fun executeAction(
        @Path("id") id: String,
        @Path("action") action: String,
        @Body params: List<Any> = emptyList()
    ): Boolean
}

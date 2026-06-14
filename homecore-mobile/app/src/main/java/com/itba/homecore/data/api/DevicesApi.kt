package com.itba.homecore.data.api

import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.DeviceLog
import com.itba.homecore.data.model.DeviceType
import okhttp3.ResponseBody
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface DevicesApi {
    @GET("devices")
    suspend fun getAllDevices(): List<Device>

    // The detail response already includes the state: do not call /state separately (E2 feedback).
    @GET("devices/{id}")
    suspend fun getDevice(@Path("id") id: String): Device

    @POST("devices")
    suspend fun createDevice(@Body body: Map<String, @JvmSuppressWildcards Any?>): Device

    @PUT("devices/{id}")
    suspend fun updateDevice(
        @Path("id") id: String,
        @Body body: Map<String, @JvmSuppressWildcards Any?>
    ): Device

    @DELETE("devices/{id}")
    suspend fun deleteDevice(@Path("id") id: String)

    // Return the raw body: the API's response shape for an action is not used, and
    // declaring a concrete type (e.g. Boolean) makes Gson throw if it differs.
    @PATCH("devices/{id}/{action}")
    suspend fun executeAction(
        @Path("id") id: String,
        @Path("action") action: String,
        @Body params: List<Any> = emptyList()
    ): ResponseBody

    /** Type catalog: maps the canonical name ("lamp", "door", ...) to the id required by POST /devices. */
    @GET("devicetypes")
    suspend fun getDeviceTypes(): List<DeviceType>

    @GET("devices/logs/limit/{limit}/offset/{offset}")
    suspend fun getAllLogs(
        @Path("limit") limit: Int,
        @Path("offset") offset: Int
    ): List<DeviceLog>
}

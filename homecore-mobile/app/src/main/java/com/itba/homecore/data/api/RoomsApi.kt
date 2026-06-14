package com.itba.homecore.data.api

import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.Room
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface RoomsApi {
    @GET("rooms")
    suspend fun getAllRooms(): List<Room>

    @GET("rooms/{id}")
    suspend fun getRoom(@Path("id") id: String): Room

    @POST("rooms")
    suspend fun createRoom(@Body body: Map<String, @JvmSuppressWildcards Any?>): Room

    @PUT("rooms/{id}")
    suspend fun updateRoom(
        @Path("id") id: String,
        @Body body: Map<String, @JvmSuppressWildcards Any?>
    ): Room

    @DELETE("rooms/{id}")
    suspend fun deleteRoom(@Path("id") id: String)

    @GET("rooms/{roomId}/devices")
    suspend fun getRoomDevices(@Path("roomId") roomId: String): List<Device>

    /** Links an existing device to the room. */
    @POST("rooms/{roomId}/devices/{deviceId}")
    suspend fun addDeviceToRoom(
        @Path("roomId") roomId: String,
        @Path("deviceId") deviceId: String
    )

    /** Unlinks the device from its current room (the route takes no roomId). */
    @DELETE("rooms/devices/{deviceId}")
    suspend fun removeDeviceFromRoom(@Path("deviceId") deviceId: String)
}

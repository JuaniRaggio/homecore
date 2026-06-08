package com.itba.homecore.data.api

import com.itba.homecore.data.model.Room
import retrofit2.http.GET

interface RoomsApi {
    @GET("rooms")
    suspend fun getAllRooms(): List<Room>
}

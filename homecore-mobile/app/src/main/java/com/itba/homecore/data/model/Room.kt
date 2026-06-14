package com.itba.homecore.data.model

import com.google.gson.annotations.SerializedName

data class Room(
    @SerializedName("id")   val id: String = "",
    @SerializedName("name") val name: String = "",
    @SerializedName("home") val home: HomeRef? = null
)

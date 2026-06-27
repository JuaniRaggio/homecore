package com.itba.homecore.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Room(
    val id: String = "",
    val name: String = "",
    val home: HomeRef? = null
)

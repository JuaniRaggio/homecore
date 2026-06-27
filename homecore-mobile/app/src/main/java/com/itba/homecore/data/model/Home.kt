package com.itba.homecore.data.model

import kotlinx.serialization.Serializable

@Serializable
data class Home(
    val id: String = "",
    val name: String = ""
)

/** Lightweight home reference embedded in a Room when the API includes it. */
@Serializable
data class HomeRef(
    val id: String = "",
    val name: String? = null
)

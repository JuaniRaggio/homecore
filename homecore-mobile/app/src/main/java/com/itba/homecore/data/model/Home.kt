package com.itba.homecore.data.model

import com.google.gson.annotations.SerializedName

data class Home(
    @SerializedName("id")       val id: String = "",
    @SerializedName("name")     val name: String = "",
    @SerializedName("metadata") val metadata: Map<String, Any?>? = null
)

/** Lightweight home reference embedded in a Room when the API includes it. */
data class HomeRef(
    @SerializedName("id")   val id: String = "",
    @SerializedName("name") val name: String? = null
)

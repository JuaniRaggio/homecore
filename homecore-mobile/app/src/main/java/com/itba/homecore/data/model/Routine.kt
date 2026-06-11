package com.itba.homecore.data.model

import com.google.gson.annotations.SerializedName

data class Routine(
    @SerializedName("id")          val id: String = "",
    @SerializedName("name")        val name: String = "",
    @SerializedName("description") val description: String? = null,
    @SerializedName("actions")     val actions: List<RoutineAction> = emptyList(),
    @SerializedName("metadata")    val metadata: RoutineMetadata? = null
)

data class RoutineAction(
    @SerializedName("device")     val device: Device? = null,
    @SerializedName("actionName") val actionName: String = "",
    @SerializedName("params")     val params: List<Any> = emptyList()
)

data class RoutineMetadata(
    @SerializedName("favorite")    val favorite: Boolean? = null,
    @SerializedName("active")      val active: Boolean? = null,
    @SerializedName("time")        val time: String? = null,
    @SerializedName("days")        val days: List<Int>? = null,
    @SerializedName("description") val description: String? = null,
    @SerializedName("homeId")      val homeId: String? = null
)

fun Routine.isFavorite(): Boolean = metadata?.favorite == true
fun Routine.isActive(): Boolean = metadata?.active ?: true
fun Routine.time(): String = metadata?.time.orEmpty()
fun Routine.days(): List<Int> = metadata?.days ?: emptyList()
fun Routine.descriptionText(): String = description ?: metadata?.description.orEmpty()

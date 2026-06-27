package com.itba.homecore.data.model

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.doubleOrNull
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonPrimitive

@Serializable
data class Routine(
    val id: String = "",
    val name: String = "",
    val description: String? = null,
    val actions: List<RoutineAction> = emptyList(),
    val time: String? = null,
    val days: List<Int>? = null,
    val metadata: RoutineMetadata? = null
)

@Serializable
data class RoutineAction(
    val device: Device? = null,
    val actionName: String = "",
    @Serializable(with = AnyListSerializer::class) val params: List<Any> = emptyList()
)

@Serializable
data class RoutineMetadata(
    val favorite: Boolean? = null,
    val active: Boolean? = null,
    val time: String? = null,
    val days: List<Int>? = null,
    val description: String? = null,
    val homeId: String? = null,
    val crossHome: Boolean? = null
)

/**
 * Routine action params come as a heterogeneous list (strings, ints, booleans) the
 * API echoes back. kotlinx.serialization can't auto-handle [Any], so we encode/decode
 * via [JsonElement] preserving the primitive type.
 */
object AnyListSerializer : KSerializer<List<Any>> {
    private val delegate = ListSerializer(JsonElement.serializer())
    override val descriptor: SerialDescriptor = delegate.descriptor

    override fun serialize(encoder: Encoder, value: List<Any>) {
        val json = encoder as? JsonEncoder ?: error("AnyListSerializer requires JSON")
        val arr = JsonArray(value.map { toJsonElement(it) })
        json.encodeJsonElement(arr)
    }

    override fun deserialize(decoder: Decoder): List<Any> {
        val json = decoder as? JsonDecoder ?: error("AnyListSerializer requires JSON")
        val arr = json.decodeJsonElement() as? JsonArray ?: return emptyList()
        return arr.mapNotNull { fromJsonElement(it) }
    }

    private fun toJsonElement(v: Any?): JsonElement = when (v) {
        null         -> JsonNull
        is JsonElement -> v
        is Boolean   -> JsonPrimitive(v)
        is Number    -> JsonPrimitive(v)
        is String    -> JsonPrimitive(v)
        else         -> JsonPrimitive(v.toString())
    }

    private fun fromJsonElement(e: JsonElement): Any? {
        if (e is JsonNull) return null
        val p = (e as? JsonPrimitive)?.takeIf { !it.isString } ?: return (e as? JsonPrimitive)?.content
        return p.booleanOrNull ?: p.intOrNull ?: p.doubleOrNull ?: p.jsonPrimitive.content
    }
}

fun Routine.isFavorite(): Boolean = metadata?.favorite == true
fun Routine.isActive(): Boolean = metadata?.active ?: true
fun Routine.time(): String =
    time?.takeIf { it.isNotBlank() } ?: metadata?.time?.takeIf { it.isNotBlank() }.orEmpty()

fun Routine.days(): List<Int> =
    days?.takeIf { it.isNotEmpty() } ?: metadata?.days ?: emptyList()

fun Routine.descriptionText(): String =
    description?.takeIf { it.isNotBlank() } ?: metadata?.description.orEmpty()

package com.itba.homecore.data.repository

import com.itba.homecore.data.api.KtorClient
import com.itba.homecore.data.api.ktorCall
import com.itba.homecore.data.api.unwrap
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.DeviceLog
import com.itba.homecore.data.model.DeviceType
import com.itba.homecore.data.model.Home
import com.itba.homecore.data.model.Room
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

/**
 * Implementation against the HCI API (Ktor), wired in [com.itba.homecore.di.AppModule].
 */
class RemoteDevicesRepository : DevicesRepository {
    private val http = KtorClient.http

    // /devicetypes is a static catalog: fetched once and cached for the session.
    @Volatile
    private var typesCache: List<DeviceType>? = null

    override suspend fun getDevices(): Result<List<Device>> = runCatching {
        ktorCall("Error al obtener dispositivos") {
            // The /devices payload carries only the type id, so resolve the full type from the
            // /devicetypes catalog. Without the name every device falls back to OTHER and shows
            // no type-specific controls; powerUsage feeds the consumption estimate.
            val devices = http.get("devices").unwrap<List<Device>>()
            val types = deviceTypes()
            devices.map { d ->
                val catalogType = types.firstOrNull { it.id == d.type.id } ?: return@map d
                d.copy(
                    type = d.type.copy(
                        name = d.type.name.ifBlank { catalogType.name },
                        powerUsage = d.type.powerUsage ?: catalogType.powerUsage
                    )
                )
            }
        }
    }

    override suspend fun executeAction(deviceId: String, action: String, params: List<Any>): Result<Unit> = runCatching {
        ktorCall("No se pudo ejecutar la acción") {
            http.patch("devices/$deviceId/$action") { setBody(JsonArray(params.map { anyToJson(it) })) }
            Unit
        }
    }

    override suspend fun setDeviceFavorite(deviceId: String, favorite: Boolean): Result<Unit> = runCatching {
        ktorCall("No se pudo actualizar el favorito") {
            // The API has no favorite endpoint: the device is re-sent via PUT with the
            // favorite flag merged into its metadata.
            val device = http.get("devices/$deviceId").unwrap<Device>()
            http.put("devices/$deviceId") { setBody(fullBody(device, favorite = favorite)) }
            Unit
        }
    }

    override suspend fun createDevice(name: String, typeName: String, roomId: String?): Result<Device> = runCatching {
        ktorCall("No se pudo crear el dispositivo") {
            // POST /devices requires the type ID, resolved by name from /devicetypes.
            val type = deviceTypes().firstOrNull { it.name.equals(typeName, ignoreCase = true) }
                ?: throw Exception("Tipo de dispositivo desconocido: $typeName")
            val body = buildJsonObject {
                put("name", name.trim())
                putJsonObject("type") { put("id", type.id) }
                if (roomId != null) putJsonObject("room") { put("id", roomId) }
            }
            http.post("devices") { setBody(body) }.unwrap<Device>()
        }
    }

    override suspend fun renameDevice(deviceId: String, newName: String): Result<Device> = runCatching {
        ktorCall("No se pudo renombrar el dispositivo") {
            val device = http.get("devices/$deviceId").unwrap<Device>()
            http.put("devices/$deviceId") { setBody(fullBody(device, name = newName.trim())) }.unwrap<Device>()
        }
    }

    override suspend fun deleteDevice(deviceId: String): Result<Unit> = runCatching {
        ktorCall("No se pudo eliminar el dispositivo") { http.delete("devices/$deviceId"); Unit }
    }

    override suspend fun getRooms(): Result<List<Room>> = runCatching {
        ktorCall("Error al obtener habitaciones") { http.get("rooms").unwrap<List<Room>>() }
    }

    override suspend fun createRoom(name: String, homeId: String?): Result<Room> = runCatching {
        ktorCall("No se pudo crear la habitación") {
            // POST /rooms takes a home reference: use the one chosen by the caller
            // or fall back to the user's first home.
            val resolvedHomeId = homeId
                ?: runCatching { http.get("homes").unwrap<List<Home>>().firstOrNull() }.getOrNull()?.id
            val body = buildJsonObject {
                put("name", name.trim())
                if (resolvedHomeId != null) putJsonObject("home") { put("id", resolvedHomeId) }
            }
            http.post("rooms") { setBody(body) }.unwrap<Room>()
        }
    }

    override suspend fun renameRoom(roomId: String, newName: String): Result<Room> = runCatching {
        ktorCall("No se pudo renombrar la habitación") {
            http.put("rooms/$roomId") { setBody(buildJsonObject { put("name", newName.trim()) }) }.unwrap<Room>()
        }
    }

    override suspend fun deleteRoom(roomId: String): Result<Unit> = runCatching {
        ktorCall("No se pudo eliminar la habitación") { http.delete("rooms/$roomId"); Unit }
    }

    override suspend fun assignDeviceToRoom(deviceId: String, roomId: String): Result<Unit> = runCatching {
        ktorCall("No se pudo vincular el dispositivo") { http.post("rooms/$roomId/devices/$deviceId"); Unit }
    }

    override suspend fun unassignDevice(deviceId: String): Result<Unit> = runCatching {
        ktorCall("No se pudo desvincular el dispositivo") { http.delete("rooms/devices/$deviceId"); Unit }
    }

    override suspend fun getLogs(limit: Int, offset: Int): Result<List<DeviceLog>> = runCatching {
        ktorCall("Error al obtener el historial") {
            http.get("devices/logs/limit/$limit/offset/$offset").unwrap<List<DeviceLog>>()
        }
    }

    private suspend fun deviceTypes(): List<DeviceType> =
        typesCache ?: http.get("devicetypes").unwrap<List<DeviceType>>().also { typesCache = it }

    /** Full PUT body as the API expects it on update. */
    private fun fullBody(
        device: Device,
        name: String = device.name,
        favorite: Boolean? = device.metadata?.favorite
    ): JsonObject = buildJsonObject {
        put("name", name)
        putJsonObject("type") { put("id", device.type.id) }
        putJsonObject("metadata") { put("favorite", favorite ?: false) }
        device.room?.id?.takeIf { it.isNotBlank() }?.let { roomId ->
            putJsonObject("room") { put("id", roomId) }
        }
    }

    private fun anyToJson(v: Any?): JsonElement = when (v) {
        null       -> JsonNull
        is Boolean -> JsonPrimitive(v)
        is Number  -> JsonPrimitive(v)
        is String  -> JsonPrimitive(v)
        else       -> JsonPrimitive(v.toString())
    }
}

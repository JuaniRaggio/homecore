package com.itba.homecore.data.repository

import com.itba.homecore.data.api.ApiClient
import com.itba.homecore.data.api.apiCall
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.DeviceLog
import com.itba.homecore.data.model.DeviceType
import com.itba.homecore.data.model.Room

/**
 * Implementation against the HCI API (Retrofit), wired in [com.itba.homecore.di.AppModule].
 */
class RemoteDevicesRepository : DevicesRepository {
    private val devicesApi = ApiClient.devicesApi
    private val roomsApi   = ApiClient.roomsApi
    private val homesApi   = ApiClient.homesApi

    // /devicetypes is a static catalog: fetched once and cached for the session.
    @Volatile
    private var typesCache: List<DeviceType>? = null

    override suspend fun getDevices(): Result<List<Device>> = runCatching {
        apiCall("Error al obtener dispositivos") {
            // The /devices payload carries only the type id, so resolve the full type from the
            // /devicetypes catalog (same as the web). Without the name every device falls back
            // to OTHER and shows no type-specific controls; powerUsage feeds the consumption estimate.
            val devices = devicesApi.getAllDevices()
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
        apiCall("No se pudo ejecutar la acción") { devicesApi.executeAction(deviceId, action, params).close() }
    }

    override suspend fun setDeviceFavorite(deviceId: String, favorite: Boolean): Result<Unit> = runCatching {
        apiCall("No se pudo actualizar el favorito") {
            // The API has no favorite endpoint: like the web app, the device is re-sent
            // via PUT with the favorite flag merged into its metadata.
            val device = devicesApi.getDevice(deviceId)
            devicesApi.updateDevice(deviceId, fullBody(device, favorite = favorite))
        }
        Unit
    }

    override suspend fun createDevice(name: String, typeName: String, roomId: String?): Result<Device> = runCatching {
        apiCall("No se pudo crear el dispositivo") {
            // POST /devices requires the type ID, resolved by name from /devicetypes.
            val type = deviceTypes().firstOrNull { it.name.equals(typeName, ignoreCase = true) }
                ?: throw Exception("Tipo de dispositivo desconocido: $typeName")
            val body = mutableMapOf<String, Any?>(
                "name" to name.trim(),
                "type" to mapOf("id" to type.id)
            )
            if (roomId != null) body["room"] = mapOf("id" to roomId)
            devicesApi.createDevice(body)
        }
    }

    override suspend fun renameDevice(deviceId: String, newName: String): Result<Device> = runCatching {
        apiCall("No se pudo renombrar el dispositivo") {
            val device = devicesApi.getDevice(deviceId)
            devicesApi.updateDevice(deviceId, fullBody(device, name = newName.trim()))
        }
    }

    override suspend fun deleteDevice(deviceId: String): Result<Unit> = runCatching {
        apiCall("No se pudo eliminar el dispositivo") { devicesApi.deleteDevice(deviceId) }
    }

    override suspend fun getRooms(): Result<List<Room>> = runCatching {
        apiCall("Error al obtener habitaciones") { roomsApi.getAllRooms() }
    }

    override suspend fun createRoom(name: String, homeId: String?): Result<Room> = runCatching {
        apiCall("No se pudo crear la habitación") {
            val body = mutableMapOf<String, Any?>("name" to name.trim())
            // POST /rooms takes a home reference: use the one chosen by the caller
            // or fall back to the user's first home, mirroring how the web creates rooms.
            val resolvedHomeId = homeId ?: runCatching { homesApi.getAllHomes().firstOrNull() }.getOrNull()?.id
            resolvedHomeId?.let { body["home"] = mapOf("id" to it) }
            roomsApi.createRoom(body)
        }
    }

    override suspend fun renameRoom(roomId: String, newName: String): Result<Room> = runCatching {
        apiCall("No se pudo renombrar la habitación") {
            roomsApi.updateRoom(roomId, mapOf("name" to newName.trim()))
        }
    }

    override suspend fun deleteRoom(roomId: String): Result<Unit> = runCatching {
        apiCall("No se pudo eliminar la habitación") { roomsApi.deleteRoom(roomId) }
    }

    override suspend fun assignDeviceToRoom(deviceId: String, roomId: String): Result<Unit> = runCatching {
        apiCall("No se pudo vincular el dispositivo") { roomsApi.addDeviceToRoom(roomId, deviceId) }
    }

    override suspend fun unassignDevice(deviceId: String): Result<Unit> = runCatching {
        apiCall("No se pudo desvincular el dispositivo") { roomsApi.removeDeviceFromRoom(deviceId) }
    }

    override suspend fun getLogs(limit: Int, offset: Int): Result<List<DeviceLog>> = runCatching {
        apiCall("Error al obtener el historial") { devicesApi.getAllLogs(limit, offset) }
    }

    private suspend fun deviceTypes(): List<DeviceType> =
        typesCache ?: devicesApi.getDeviceTypes().also { typesCache = it }

    /** Full PUT body as the API expects it (same shape the web app sends on update). */
    private fun fullBody(
        device: Device,
        name: String = device.name,
        favorite: Boolean? = device.metadata?.favorite
    ): Map<String, Any?> {
        val body = mutableMapOf<String, Any?>(
            "name" to name,
            "type" to mapOf("id" to device.type.id),
            "metadata" to mapOf("favorite" to (favorite ?: false))
        )
        device.room?.id?.takeIf { it.isNotBlank() }?.let { body["room"] = mapOf("id" to it) }
        return body
    }
}

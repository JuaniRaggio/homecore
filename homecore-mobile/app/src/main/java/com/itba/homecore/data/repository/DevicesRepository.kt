package com.itba.homecore.data.repository

import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.DeviceLog
import com.itba.homecore.data.model.Room

/**
 * Data-layer contract for devices and rooms. UI and ViewModels depend on this
 * abstraction, never on a concrete implementation: [com.itba.homecore.di.AppModule]
 * decides between [MockDevicesRepository] (prototype data) and
 * [RemoteDevicesRepository] (HCI API) — the UI does not change.
 */
interface DevicesRepository {
    suspend fun getDevices(): Result<List<Device>>
    suspend fun getDevice(id: String): Result<Device>
    suspend fun executeAction(deviceId: String, action: String, params: List<Any> = emptyList()): Result<Unit>
    suspend fun setDeviceFavorite(deviceId: String, favorite: Boolean): Result<Unit>

    /** Creates a device of the given type (canonical key: "lamp", "door", ...). */
    suspend fun createDevice(name: String, typeName: String, roomId: String?): Result<Device>

    /** Renames a device keeping the rest of its data (RF7). */
    suspend fun renameDevice(deviceId: String, newName: String): Result<Device>

    suspend fun deleteDevice(deviceId: String): Result<Unit>

    suspend fun getRooms(): Result<List<Room>>

    /** Creates a room. */
    suspend fun createRoom(name: String): Result<Room>

    suspend fun renameRoom(roomId: String, newName: String): Result<Room>

    suspend fun deleteRoom(roomId: String): Result<Unit>

    /** Links an existing device to a room (RF16). */
    suspend fun assignDeviceToRoom(deviceId: String, roomId: String): Result<Unit>

    /** Unlinks a device from its current room (RF16). */
    suspend fun unassignDevice(deviceId: String): Result<Unit>

    /** Global action history (RF13). */
    suspend fun getLogs(limit: Int = 20, offset: Int = 0): Result<List<DeviceLog>>
}

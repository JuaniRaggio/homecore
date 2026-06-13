package com.itba.homecore.data.repository

import com.itba.homecore.data.mock.MockData
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.DeviceLog
import com.itba.homecore.data.model.DeviceMeta
import com.itba.homecore.data.model.DeviceState
import com.itba.homecore.data.model.DeviceType
import com.itba.homecore.data.model.Room
import com.itba.homecore.data.model.RoomRef
import kotlinx.coroutines.delay
import java.time.Instant
import java.util.UUID

/**
 * Prototype implementation: serves local [MockData] without touching the network.
 * A small delay simulates latency so loading states can be exercised.
 */
class MockDevicesRepository : DevicesRepository {

    override suspend fun getDevices(): Result<List<Device>> = runCatching {
        delay(300)
        MockData.devices.toList()
    }

    override suspend fun getDevice(id: String): Result<Device> = runCatching {
        delay(150)
        MockData.devices.firstOrNull { it.id == id }
            ?: throw Exception("Dispositivo no encontrado")
    }

    override suspend fun executeAction(deviceId: String, action: String, params: List<Any>): Result<Unit> = runCatching {
        delay(200)
        val idx = MockData.devices.indexOfFirst { it.id == deviceId }
        if (idx >= 0) {
            val device = MockData.devices[idx]
            val newStatus = MockData.statusForAction(action)
            MockData.devices[idx] = device.copy(
                state = (device.state ?: DeviceState()).copy(status = newStatus)
            )
        }
    }

    override suspend fun setDeviceFavorite(deviceId: String, favorite: Boolean): Result<Unit> = runCatching {
        val idx = MockData.devices.indexOfFirst { it.id == deviceId }
        if (idx >= 0) {
            MockData.devices[idx] = MockData.devices[idx].copy(metadata = DeviceMeta(favorite = favorite))
        }
    }

    override suspend fun createDevice(name: String, typeName: String, roomId: String?): Result<Device> = runCatching {
        delay(300)
        require(name.isNotBlank()) { "El nombre es obligatorio" }
        val room = roomId?.let { id -> MockData.rooms.firstOrNull { it.id == id } }
        val device = Device(
            id = "d-" + UUID.randomUUID().toString().take(8),
            name = name.trim(),
            type = DeviceType(name = typeName),
            state = DeviceState(status = MockData.defaultStatusFor(typeName)),
            room = room?.let { RoomRef(it.id, it.name) },
            metadata = DeviceMeta(favorite = false)
        )
        MockData.devices.add(device)
        device
    }

    override suspend fun renameDevice(deviceId: String, newName: String): Result<Device> = runCatching {
        delay(200)
        require(newName.isNotBlank()) { "El nombre es obligatorio" }
        val idx = MockData.devices.indexOfFirst { it.id == deviceId }
        require(idx >= 0) { "Dispositivo no encontrado" }
        val updated = MockData.devices[idx].copy(name = newName.trim())
        MockData.devices[idx] = updated
        updated
    }

    override suspend fun deleteDevice(deviceId: String): Result<Unit> = runCatching {
        delay(200)
        MockData.devices.removeAll { it.id == deviceId }
        Unit
    }

    override suspend fun getRooms(): Result<List<Room>> = runCatching {
        delay(150)
        MockData.rooms.toList()
    }

    override suspend fun createRoom(name: String): Result<Room> = runCatching {
        delay(300)
        require(name.isNotBlank()) { "El nombre es obligatorio" }
        val room = Room(id = "r-" + UUID.randomUUID().toString().take(8), name = name.trim())
        MockData.rooms.add(room)
        room
    }

    override suspend fun renameRoom(roomId: String, newName: String): Result<Room> = runCatching {
        delay(200)
        require(newName.isNotBlank()) { "El nombre es obligatorio" }
        val idx = MockData.rooms.indexOfFirst { it.id == roomId }
        require(idx >= 0) { "Habitación no encontrada" }
        val updated = MockData.rooms[idx].copy(name = newName.trim())
        MockData.rooms[idx] = updated
        // Keep the embedded room reference of the devices in sync.
        MockData.devices.replaceAll { device ->
            if (device.room?.id == roomId) device.copy(room = RoomRef(roomId, updated.name)) else device
        }
        updated
    }

    override suspend fun deleteRoom(roomId: String): Result<Unit> = runCatching {
        delay(200)
        MockData.rooms.removeAll { it.id == roomId }
        // Devices of a deleted room become unassigned, like in the real API.
        MockData.devices.replaceAll { device ->
            if (device.room?.id == roomId) device.copy(room = null) else device
        }
        Unit
    }

    override suspend fun assignDeviceToRoom(deviceId: String, roomId: String): Result<Unit> = runCatching {
        delay(200)
        val room = MockData.rooms.firstOrNull { it.id == roomId }
            ?: throw Exception("Habitación no encontrada")
        val idx = MockData.devices.indexOfFirst { it.id == deviceId }
        require(idx >= 0) { "Dispositivo no encontrado" }
        MockData.devices[idx] = MockData.devices[idx].copy(room = RoomRef(room.id, room.name))
    }

    override suspend fun unassignDevice(deviceId: String): Result<Unit> = runCatching {
        delay(200)
        val idx = MockData.devices.indexOfFirst { it.id == deviceId }
        require(idx >= 0) { "Dispositivo no encontrado" }
        MockData.devices[idx] = MockData.devices[idx].copy(room = null)
    }

    override suspend fun getLogs(limit: Int, offset: Int): Result<List<DeviceLog>> = runCatching {
        delay(200)
        // Synthesizes a recent-looking history from the current devices.
        val now = Instant.now()
        MockData.devices.take(limit).mapIndexed { i, device ->
            DeviceLog(
                id = "log-$i",
                deviceId = device.id,
                device = device,
                actionName = if (i % 2 == 0) "turnOn" else "turnOff",
                params = emptyList(),
                timestamp = now.minusSeconds((i + 1) * 300L).toString()
            )
        }
    }
}

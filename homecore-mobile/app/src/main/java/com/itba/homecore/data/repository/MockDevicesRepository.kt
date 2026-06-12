package com.itba.homecore.data.repository

import com.itba.homecore.data.mock.MockData
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.DeviceMeta
import com.itba.homecore.data.model.DeviceState
import com.itba.homecore.data.model.DeviceType
import com.itba.homecore.data.model.Room
import com.itba.homecore.data.model.RoomRef
import kotlinx.coroutines.delay
import java.util.UUID

/**
 * Implementación de prototipo: sirve datos locales de [MockData] sin tocar la red.
 * Aplica un pequeño delay para simular latencia y poder ver el estado de carga.
 */
class MockDevicesRepository : DevicesRepository {

    override suspend fun getDevices(): Result<List<Device>> = runCatching {
        delay(300)
        MockData.devices.toList()
    }

    override suspend fun getRooms(): Result<List<Room>> = runCatching {
        delay(150)
        MockData.rooms
    }

    override suspend fun executeAction(deviceId: String, action: String, params: List<Any>): Result<Unit> = runCatching {
        delay(200)
        val idx = MockData.devices.indexOfFirst { it.id == deviceId }
        if (idx >= 0) {
            val device = MockData.devices[idx]
            val newStatus = MockData.statusForAction(action)
            MockData.devices[idx] = device.copy(
                state = (device.state ?: com.itba.homecore.data.model.DeviceState()).copy(status = newStatus)
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

    override suspend fun createRoom(name: String): Result<Room> = runCatching {
        delay(300)
        require(name.isNotBlank()) { "El nombre es obligatorio" }
        val room = Room(id = "r-" + UUID.randomUUID().toString().take(8), name = name.trim())
        MockData.rooms.add(room)
        room
    }
}

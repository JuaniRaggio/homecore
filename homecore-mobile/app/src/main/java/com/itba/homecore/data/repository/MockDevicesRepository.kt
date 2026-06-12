package com.itba.homecore.data.repository

import com.itba.homecore.data.mock.MockData
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.DeviceMeta
import com.itba.homecore.data.model.Room
import kotlinx.coroutines.delay

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
}

package com.itba.homecore.data.repository

import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.Room

/**
 * Contrato de la capa de datos de dispositivos. La UI/ViewModel dependen de esta
 * abstracción, no de una implementación concreta. Hoy se resuelve con datos mock
 * ([MockDevicesRepository]); para conectar el backend basta con cambiar el flag en
 * [com.itba.homecore.di.AppModule] — la UI no se modifica.
 */
interface DevicesRepository {
    suspend fun getDevices(): Result<List<Device>>
    suspend fun getRooms(): Result<List<Room>>
    suspend fun executeAction(deviceId: String, action: String, params: List<Any> = emptyList()): Result<Unit>
    suspend fun setDeviceFavorite(deviceId: String, favorite: Boolean): Result<Unit>

    /** Crea un dispositivo del tipo indicado (clave canónica: "lamp", "door", …). */
    suspend fun createDevice(name: String, typeName: String, roomId: String?): Result<Device>

    /** Crea una habitación. */
    suspend fun createRoom(name: String): Result<Room>
}

package com.itba.homecore.data.repository

import com.itba.homecore.data.api.ApiClient
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.Room
import retrofit2.HttpException

/**
 * Implementación real contra la API HCI (Retrofit). Se activa poniendo
 * USE_MOCK = false en [com.itba.homecore.di.AppModule].
 */
class RemoteDevicesRepository : DevicesRepository {
    private val devicesApi = ApiClient.devicesApi
    private val roomsApi   = ApiClient.roomsApi

    override suspend fun getDevices(): Result<List<Device>> = runCatching {
        try { devicesApi.getAllDevices() }
        catch (e: HttpException) { throw Exception(httpMsg(e, "Error al obtener dispositivos")) }
    }

    override suspend fun getRooms(): Result<List<Room>> = runCatching {
        try { roomsApi.getAllRooms() }
        catch (e: HttpException) { throw Exception(httpMsg(e, "Error al obtener habitaciones")) }
    }

    override suspend fun executeAction(deviceId: String, action: String, params: List<Any>): Result<Unit> = runCatching {
        try { devicesApi.executeAction(deviceId, action, params); Unit }
        catch (e: HttpException) { throw Exception(httpMsg(e, "No se pudo ejecutar la acción")) }
    }

    override suspend fun setDeviceFavorite(deviceId: String, favorite: Boolean): Result<Unit> = runCatching {
        // TODO: la API HCI marca favoritos vía metadata del dispositivo (PUT /devices/{id}).
        // Por ahora es optimista: la UI ya refleja el cambio y al reconectar se agrega la llamada.
        Unit
    }

    private fun httpMsg(e: HttpException, fallback: String) =
        try { e.response()?.errorBody()?.string() ?: fallback } catch (_: Exception) { fallback }
}

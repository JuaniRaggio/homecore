package com.itba.homecore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.DeviceCapabilities
import com.itba.homecore.data.model.DeviceLog
import com.itba.homecore.data.model.Room
import com.itba.homecore.data.model.category
import com.itba.homecore.data.model.isFavorite
import com.itba.homecore.data.repository.DevicesRepository
import com.itba.homecore.di.AppModule
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class DevicesUiState {
    object Loading : DevicesUiState()
    data class Success(
        val rooms: List<Room>,
        val devices: List<Device>
    ) : DevicesUiState()
    data class Error(val message: String) : DevicesUiState()
}

/**
 * Depends on the [DevicesRepository] abstraction, not on a concrete implementation.
 * The no-arg secondary constructor resolves the repo from [AppModule] so that
 * `viewModel()` can instantiate it via reflection; the primary one allows injecting
 * a fake in tests.
 */
class DevicesViewModel(
    private val repository: DevicesRepository
) : ViewModel() {

    constructor() : this(AppModule.devicesRepository)

    private val _state = MutableStateFlow<DevicesUiState>(DevicesUiState.Loading)
    val state: StateFlow<DevicesUiState> = _state.asStateFlow()

    /** Action history, shown on the profile screen. */
    private val _logs = MutableStateFlow<List<DeviceLog>>(emptyList())
    val logs: StateFlow<List<DeviceLog>> = _logs.asStateFlow()

    init { load() }

    /** Loads the recent action history from the API. */
    fun loadLogs(limit: Int = 10) {
        viewModelScope.launch {
            repository.getLogs(limit, 0).onSuccess { _logs.value = it }
        }
    }

    fun load() = refresh(showLoading = true)

    /**
     * Loads rooms and devices. The two calls are independent, so they run concurrently.
     * [showLoading] is false for refreshes after an action, to avoid flashing the spinner.
     */
    private fun refresh(showLoading: Boolean) {
        viewModelScope.launch {
            if (showLoading) _state.value = DevicesUiState.Loading
            val (roomsRes, devicesRes) = coroutineScope {
                val rooms   = async { repository.getRooms() }
                val devices = async { repository.getDevices() }
                rooms.await() to devices.await()
            }

            val rooms   = roomsRes.getOrNull()
            val devices = devicesRes.getOrNull()

            if (rooms == null) {
                _state.value = DevicesUiState.Error(roomsRes.exceptionOrNull()?.message ?: "Error al cargar")
                return@launch
            }
            if (devices == null) {
                _state.value = DevicesUiState.Error(devicesRes.exceptionOrNull()?.message ?: "Error al cargar")
                return@launch
            }
            _state.value = DevicesUiState.Success(rooms, devices)
        }
    }

    /**
     * Card quick switch. The on/off-equivalent action per type comes from the shared
     * [DeviceCapabilities] (mirrors the web), so e.g. a curtain sends up/down — never on/off.
     */
    fun toggleDevice(device: Device, turnOn: Boolean) {
        val toggle = DeviceCapabilities.quickToggle(device.category()) ?: return
        val action = if (turnOn) toggle.onAction else toggle.offAction
        runAction(device.id, action)
    }

    /** Runs an arbitrary device action (used by the detail screen) and refreshes on success. */
    fun runAction(deviceId: String, action: String, params: List<Any> = emptyList()) {
        viewModelScope.launch {
            repository.executeAction(deviceId, action, params)
                .onSuccess { refresh(showLoading = false) }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo ejecutar la acción") }
        }
    }

    fun toggleFavorite(device: Device) {
        viewModelScope.launch {
            repository.setDeviceFavorite(device.id, !device.isFavorite())
                .onSuccess { refresh(showLoading = false) }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo actualizar el favorito") }
        }
    }

    /** Creates a device and reloads the list. [onDone] runs only on success (keeps the sheet open on error). */
    fun createDevice(name: String, typeName: String, roomId: String?, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.createDevice(name, typeName, roomId)
                .onSuccess {
                    load()
                    onDone()
                }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo crear el dispositivo") }
        }
    }

    /**
     * Creates a room (optionally linked to [homeId]) and reloads the list.
     * [onDone] runs only on success (keeps the sheet open on error).
     */
    fun createRoom(name: String, homeId: String? = null, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.createRoom(name, homeId)
                .onSuccess {
                    load()
                    onDone()
                }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo crear la habitación") }
        }
    }

    // -- Device management ----------------------------------------------------------
    fun renameDevice(deviceId: String, newName: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.renameDevice(deviceId, newName)
                .onSuccess { refresh(showLoading = false); onDone() }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo renombrar el dispositivo") }
        }
    }

    fun deleteDevice(deviceId: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteDevice(deviceId)
                .onSuccess { refresh(showLoading = false); onDone() }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo eliminar el dispositivo") }
        }
    }

    /** Links the device to [roomId], or unlinks it when [roomId] is null. */
    fun setDeviceRoom(deviceId: String, roomId: String?, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            val result = if (roomId == null) repository.unassignDevice(deviceId)
                         else repository.assignDeviceToRoom(deviceId, roomId)
            result
                .onSuccess { refresh(showLoading = false); onDone() }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo mover el dispositivo") }
        }
    }

    // -- Room management ------------------------------------------------------------
    fun renameRoom(roomId: String, newName: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.renameRoom(roomId, newName)
                .onSuccess { refresh(showLoading = false); onDone() }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo renombrar la habitación") }
        }
    }

    fun deleteRoom(roomId: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            repository.deleteRoom(roomId)
                .onSuccess { refresh(showLoading = false); onDone() }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo eliminar la habitación") }
        }
    }
}

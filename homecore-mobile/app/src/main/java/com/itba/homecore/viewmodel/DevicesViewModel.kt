package com.itba.homecore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.Room
import com.itba.homecore.data.repository.DevicesRepository
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

class DevicesViewModel : ViewModel() {
    private val repository = DevicesRepository()

    private val _state = MutableStateFlow<DevicesUiState>(DevicesUiState.Loading)
    val state: StateFlow<DevicesUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = DevicesUiState.Loading
            val roomsRes   = repository.getRooms()
            val devicesRes = repository.getDevices()

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

    fun toggleDevice(device: Device, turnOn: Boolean) {
        viewModelScope.launch {
            val action = when (device.type.name.lowercase()) {
                "lamp", "ac", "speaker", "oven" -> if (turnOn) "turnOn" else "turnOff"
                "door", "faucet"                 -> if (turnOn) "open"   else "close"
                "lock"                            -> if (turnOn) "unlock" else "lock"
                "blinds"                          -> if (turnOn) "open"   else "close"
                "alarm"                           -> if (turnOn) "armAway" else "disarm"
                "vacuum"                          -> if (turnOn) "start"  else "pause"
                else                              -> if (turnOn) "turnOn" else "turnOff"
            }
            // Optimistic refresh — execute, then reload to get the real state
            repository.executeAction(device.id, action).onSuccess { load() }
        }
    }
}

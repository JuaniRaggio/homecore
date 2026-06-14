package com.itba.homecore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itba.homecore.data.model.Home
import com.itba.homecore.data.model.Room
import com.itba.homecore.data.repository.DevicesRepository
import com.itba.homecore.data.repository.HomesRepository
import com.itba.homecore.di.AppModule
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomesUiState {
    object Loading : HomesUiState()
    data class Success(val homes: List<Home>, val rooms: List<Room>) : HomesUiState()
    data class Error(val message: String) : HomesUiState()
}

/**
 * Homes feature (RF17-RF19). Manages homes (CRUD) and creates rooms linked to a home.
 * Rooms come from [DevicesRepository] so the screen can show which rooms belong to each
 * home (RF18/RF19), without duplicating the rooms data source.
 */
class HomesViewModel(
    private val homesRepository: HomesRepository,
    private val devicesRepository: DevicesRepository
) : ViewModel() {

    constructor() : this(AppModule.homesRepository, AppModule.devicesRepository)

    private val _state = MutableStateFlow<HomesUiState>(HomesUiState.Loading)
    val state: StateFlow<HomesUiState> = _state.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = HomesUiState.Loading
            val (homesRes, roomsRes) = coroutineScope {
                val homes = async { homesRepository.getHomes() }
                val rooms = async { devicesRepository.getRooms() }
                homes.await() to rooms.await()
            }
            val homes = homesRes.getOrNull()
            if (homes == null) {
                _state.value = HomesUiState.Error(homesRes.exceptionOrNull()?.message ?: "Error al cargar")
                return@launch
            }
            _state.value = HomesUiState.Success(homes, roomsRes.getOrNull() ?: emptyList())
        }
    }

    fun createHome(name: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            homesRepository.createHome(name)
                .onSuccess { load(); onDone() }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo crear el hogar") }
        }
    }

    fun renameHome(id: String, newName: String) {
        viewModelScope.launch {
            homesRepository.renameHome(id, newName)
                .onSuccess { load() }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo renombrar el hogar") }
        }
    }

    fun deleteHome(id: String) {
        viewModelScope.launch {
            homesRepository.deleteHome(id)
                .onSuccess { load() }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo eliminar el hogar") }
        }
    }

    /** Creates a room linked to [homeId] (RF19) and reloads. */
    fun createRoom(name: String, homeId: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            devicesRepository.createRoom(name, homeId)
                .onSuccess { load(); onDone() }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo crear la habitación") }
        }
    }
}

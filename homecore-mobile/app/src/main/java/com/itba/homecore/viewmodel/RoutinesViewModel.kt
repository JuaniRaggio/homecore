package com.itba.homecore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itba.homecore.data.model.Routine
import com.itba.homecore.data.repository.RoutinesRepository
import com.itba.homecore.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class RoutinesUiState {
    object Loading : RoutinesUiState()
    data class Success(val routines: List<Routine>) : RoutinesUiState()
    data class Error(val message: String) : RoutinesUiState()
}

/** See [DevicesViewModel] for the interface-based injection pattern. */
class RoutinesViewModel(
    private val repository: RoutinesRepository
) : ViewModel() {

    constructor() : this(AppModule.routinesRepository)

    private val _state = MutableStateFlow<RoutinesUiState>(RoutinesUiState.Loading)
    val state: StateFlow<RoutinesUiState> = _state.asStateFlow()

    private val _executingId = MutableStateFlow<String?>(null)
    val executingId: StateFlow<String?> = _executingId.asStateFlow()

    // Null means "no home selected → show all". When set, only routines with
    // metadata.homeId == currentHomeId (or with no homeId assigned yet) are shown.
    private var currentHomeId: String? = null

    init { load() }

    /**
     * Switches the active home filter and reloads.
     * Routines are scoped to a home via [com.itba.homecore.data.model.RoutineMetadata.homeId].
     * Passing null clears the filter and shows all routines.
     */
    fun loadForHome(homeId: String?) {
        currentHomeId = homeId
        load()
    }

    fun load() {
        viewModelScope.launch {
            _state.value = RoutinesUiState.Loading
            repository.getRoutines()
                .onSuccess { all ->
                    val homeId = currentHomeId
                    // Routines with homeId == null are treated as shared (visible in every home).
                    // Routines with a homeId are exclusive to that home.
                    val routines = if (homeId == null) all
                        else all.filter { r -> r.metadata?.homeId == null || r.metadata.homeId == homeId }
                    _state.value = RoutinesUiState.Success(routines)
                }
                .onFailure { _state.value = RoutinesUiState.Error(it.message ?: "Error al cargar rutinas") }
        }
    }

    fun execute(routine: Routine) {
        viewModelScope.launch {
            _executingId.value = routine.id
            repository.executeRoutine(routine.id)
                .onSuccess {
                    UiMessages.emit("Rutina ejecutada")
                    NotificationEvents.emit("HomeCore", "Rutina ejecutada: ${routine.name}")
                }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo ejecutar la rutina") }
            _executingId.value = null
        }
    }

    fun toggleFavorite(routine: Routine) {
        viewModelScope.launch {
            repository.toggleFavorite(routine)
                .onSuccess { load() }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo marcar como favorita") }
        }
    }

    fun toggleActive(routine: Routine) {
        viewModelScope.launch {
            repository.toggleActive(routine)
                .onSuccess { load() }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo cambiar el estado") }
        }
    }

    /**
     * Deletes the routine from the API. Because [com.itba.homecore.data.model.RoutineMetadata.homeId]
     * ties each routine to a home, removing it from the API removes it from this home's view.
     */
    fun deleteRoutine(routineId: String) {
        viewModelScope.launch {
            repository.deleteRoutine(routineId)
                .onSuccess { load() }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo eliminar la rutina") }
        }
    }
}

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

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = RoutinesUiState.Loading
            repository.getRoutines()
                .onSuccess { _state.value = RoutinesUiState.Success(it) }
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
                    // Tell DevicesViewModel to refetch: the execute endpoint does not
                    // return new device state, so without this the UI stays stale even
                    // when the backend did fire the actions.
                    RoutineExecutionEvents.emit()
                }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo ejecutar la rutina") }
            _executingId.value = null
        }
    }

    fun toggleFavorite(routine: Routine) {
        viewModelScope.launch {
            repository.toggleFavorite(routine)
                .onSuccess { replaceRoutine(it) }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo marcar como favorita") }
        }
    }

    fun toggleActive(routine: Routine) {
        viewModelScope.launch {
            repository.toggleActive(routine)
                .onSuccess { replaceRoutine(it) }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo cambiar el estado") }
        }
    }

    /** Swaps a single routine in the loaded list in place, avoiding a full reload (no flicker). */
    private fun replaceRoutine(updated: Routine) {
        val current = _state.value as? RoutinesUiState.Success ?: return
        _state.value = current.copy(
            routines = current.routines.map { if (it.id == updated.id) updated else it }
        )
    }
}

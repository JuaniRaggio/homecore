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
            _executingId.value = null
        }
    }

    fun toggleFavorite(routine: Routine) {
        viewModelScope.launch {
            repository.toggleFavorite(routine).onSuccess { load() }
        }
    }

    fun toggleActive(routine: Routine) {
        viewModelScope.launch {
            repository.toggleActive(routine).onSuccess { load() }
        }
    }
}

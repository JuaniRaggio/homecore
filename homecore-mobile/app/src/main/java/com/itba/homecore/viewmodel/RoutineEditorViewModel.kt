package com.itba.homecore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itba.homecore.data.model.Device
import com.itba.homecore.data.model.Routine
import com.itba.homecore.data.model.RoutineAction
import com.itba.homecore.data.model.RoutineMetadata
import com.itba.homecore.data.repository.DevicesRepository
import com.itba.homecore.data.repository.RoutinesRepository
import com.itba.homecore.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Editable state for a single routine, used both to create a new one and to edit an
 * existing one (detail screen). Holds the working copy of every field plus the device
 * list needed by the action picker.
 */
data class RoutineEditorState(
    val loading: Boolean = true,
    val saving: Boolean = false,
    val isNew: Boolean = true,
    val name: String = "",
    val description: String = "",
    val time: String = "",
    val days: Set<Int> = emptySet(),
    val active: Boolean = true,
    val favorite: Boolean = false,
    val actions: List<RoutineAction> = emptyList(),
    val devices: List<Device> = emptyList()
)

class RoutineEditorViewModel(
    private val routinesRepository: RoutinesRepository,
    private val devicesRepository: DevicesRepository
) : ViewModel() {

    constructor() : this(AppModule.routinesRepository, AppModule.devicesRepository)

    private val _state = MutableStateFlow(RoutineEditorState())
    val state: StateFlow<RoutineEditorState> = _state.asStateFlow()

    private var routineId: String? = null

    /** [id] null starts a blank routine (create); otherwise loads it for editing. */
    fun start(id: String?) {
        routineId = id
        viewModelScope.launch {
            val devices = devicesRepository.getDevices().getOrNull() ?: emptyList()
            if (id == null) {
                _state.value = RoutineEditorState(loading = false, isNew = true, devices = devices)
                return@launch
            }
            val routine = routinesRepository.getRoutine(id).getOrNull()
            if (routine == null) {
                _state.update { it.copy(loading = false, devices = devices) }
                return@launch
            }
            _state.value = RoutineEditorState(
                loading = false,
                isNew = false,
                name = routine.name,
                description = routine.description ?: routine.metadata?.description.orEmpty(),
                time = routine.metadata?.time.orEmpty(),
                days = routine.metadata?.days?.toSet() ?: emptySet(),
                active = routine.metadata?.active ?: true,
                favorite = routine.metadata?.favorite ?: false,
                actions = routine.actions,
                devices = devices
            )
        }
    }

    fun setName(v: String) = _state.update { it.copy(name = v) }
    fun setDescription(v: String) = _state.update { it.copy(description = v) }
    fun setTime(v: String) = _state.update { it.copy(time = v) }
    fun setActive(v: Boolean) = _state.update { it.copy(active = v) }
    fun setFavorite(v: Boolean) = _state.update { it.copy(favorite = v) }

    fun toggleDay(day: Int) = _state.update {
        it.copy(days = if (day in it.days) it.days - day else it.days + day)
    }

    fun addAction(device: Device, actionName: String) = _state.update {
        it.copy(actions = it.actions + RoutineAction(device = device, actionName = actionName, params = emptyList()))
    }

    fun removeAction(index: Int) = _state.update {
        it.copy(actions = it.actions.filterIndexed { i, _ -> i != index })
    }

    /** Builds the working routine and persists it (create or update). */
    fun save(onDone: () -> Unit) {
        val s = _state.value
        if (s.name.isBlank()) {
            UiMessages.emit("La rutina necesita un nombre")
            return
        }
        val normalizedTime = normalizeTime(s.time)
        // The API rejects routines with no actions ("Actions array cannot be empty").
        if (s.actions.isEmpty()) {
            UiMessages.emit("Agregá al menos una acción a la rutina")
            return
        }
        val routine = Routine(
            id = routineId.orEmpty(),
            name = s.name.trim(),
            description = s.description.trim().ifBlank { null },
            actions = s.actions,
            metadata = RoutineMetadata(
                favorite = s.favorite,
                active = s.active,
                time = normalizedTime,
                days = s.days.sorted(),
                description = s.description.trim().ifBlank { null }
            )
        )
        viewModelScope.launch {
            _state.update { it.copy(saving = true) }
            val result = if (s.isNew) routinesRepository.createRoutine(routine)
                         else routinesRepository.saveRoutine(routine)
            _state.update { it.copy(saving = false) }
            result
                .onSuccess { onDone() }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo guardar la rutina") }
        }
    }

    fun delete(onDone: () -> Unit) {
        val id = routineId ?: return
        viewModelScope.launch {
            routinesRepository.deleteRoutine(id)
                .onSuccess { onDone() }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo eliminar la rutina") }
        }
    }

    /** "8:0" -> "08:00", "08:00:00" -> "08:00", "" -> null. */
    private fun normalizeTime(raw: String): String? {
        val s = raw.trim()
        if (s.isBlank()) return null
        val parts = s.split(":")
        if (parts.size < 2) return s
        val hh = parts[0].trim().padStart(2, '0')
        val mm = parts[1].trim().padStart(2, '0').take(2)
        return "$hh:$mm"
    }

    fun execute() {
        val id = routineId ?: return
        viewModelScope.launch {
            routinesRepository.executeRoutine(id)
                .onSuccess {
                    UiMessages.emit("Rutina ejecutada")
                    RoutineExecutionEvents.emit()
                }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo ejecutar la rutina") }
        }
    }
}

package com.itba.homecore.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.itba.homecore.data.model.Home
import com.itba.homecore.data.repository.HomesRepository
import com.itba.homecore.di.AppModule
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class HomesUiState {
    object Loading : HomesUiState()
    data class Success(
        val homes: List<Home>,
        val selectedHome: Home? = null
    ) : HomesUiState()
    data class Error(val message: String) : HomesUiState()
}

/**
 * Homes feature. Backs the [com.itba.homecore.ui.components.HouseHeader] dropdown, which is the
 * only entry point to homes: it lists them, switches the active one and creates new ones.
 * There is no dedicated Homes screen.
 */
class HomesViewModel(
    private val homesRepository: HomesRepository
) : ViewModel() {

    constructor() : this(AppModule.homesRepository)

    private val _state = MutableStateFlow<HomesUiState>(HomesUiState.Loading)
    val state: StateFlow<HomesUiState> = _state.asStateFlow()

    private var currentSelectedHome: Home? = null

    init { load() }

    fun load() {
        viewModelScope.launch {
            _state.value = HomesUiState.Loading
            homesRepository.getHomes()
                .onSuccess { homes ->
                    // Preserve the selection if the home still exists after reload; otherwise default
                    // to the first home. The app always operates within a home, so there is no real
                    // "no home" browsing state.
                    val selection = currentSelectedHome?.let { sel -> homes.find { it.id == sel.id } }
                        ?: homes.firstOrNull()
                    currentSelectedHome = selection
                    _state.value = HomesUiState.Success(homes, selection)
                }
                .onFailure { _state.value = HomesUiState.Error(it.message ?: "Error al cargar") }
        }
    }

    /** Marks [home] as the active home and notifies all observers. */
    fun selectHome(home: Home) {
        currentSelectedHome = home
        val current = _state.value as? HomesUiState.Success ?: return
        _state.value = current.copy(selectedHome = home)
    }

    fun createHome(name: String, onDone: () -> Unit = {}) {
        viewModelScope.launch {
            homesRepository.createHome(name)
                .onSuccess { load(); onDone() }
                .onFailure { UiMessages.emit(it.message ?: "No se pudo crear el hogar") }
        }
    }
}

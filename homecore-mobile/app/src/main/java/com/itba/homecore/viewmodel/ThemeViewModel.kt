package com.itba.homecore.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.itba.homecore.data.local.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ThemeViewModel(app: Application) : AndroidViewModel(app) {

    private val session = SessionManager(app.applicationContext)

    private val _isDarkTheme = MutableStateFlow(true)
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    init {
        viewModelScope.launch {
            // null → first launch → default to dark
            _isDarkTheme.value = session.getDarkTheme() ?: true
        }
    }

    fun setDarkTheme(dark: Boolean) {
        _isDarkTheme.value = dark
        viewModelScope.launch { session.saveDarkTheme(dark) }
    }
}

package com.itba.homecore.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.itba.homecore.data.api.ApiClient

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "homecore_prefs")

class SessionManager(private val context: Context) {
    companion object {
        private val TOKEN_KEY = stringPreferencesKey("auth_token")
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
        ApiClient.setToken(token)
    }

    suspend fun saveUserInfo(id: String, name: String, email: String) { /* for future use */ }
}

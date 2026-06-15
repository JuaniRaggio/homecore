package com.itba.homecore.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.itba.homecore.data.api.ApiClient
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "homecore_prefs")

class SessionManager(private val context: Context) {
    companion object {
        private val TOKEN_KEY      = stringPreferencesKey("auth_token")
        private val USER_ID_KEY    = stringPreferencesKey("user_id")
        private val USER_NAME_KEY  = stringPreferencesKey("user_name")
        private val USER_EMAIL_KEY = stringPreferencesKey("user_email")
        private val LANGUAGE_KEY   = stringPreferencesKey("language")
    }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[TOKEN_KEY] = token }
        ApiClient.setToken(token)
    }

    suspend fun saveUserInfo(id: String, name: String, email: String) {
        context.dataStore.edit {
            it[USER_ID_KEY]    = id
            it[USER_NAME_KEY]  = name
            it[USER_EMAIL_KEY] = email
        }
    }

    suspend fun getToken(): String? = context.dataStore.data.first()[TOKEN_KEY]

    suspend fun getUserName(): String? = context.dataStore.data.first()[USER_NAME_KEY]

    suspend fun getUserEmail(): String? = context.dataStore.data.first()[USER_EMAIL_KEY]

    suspend fun saveLanguage(code: String) { context.dataStore.edit { it[LANGUAGE_KEY] = code } }
    suspend fun getLanguage(): String? = context.dataStore.data.first()[LANGUAGE_KEY]

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}

package com.Lkmobile.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserPreferences(private val context: Context) {

    companion object {
        val ACCESS_ID = stringPreferencesKey("access_id")
        val USER_ID = stringPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val HAS_NAME = booleanPreferencesKey("has_name")
        val SERVER_URL = stringPreferencesKey("server_url")
    }

    val accessId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[ACCESS_ID] ?: ""
    }

    val userId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_ID] ?: ""
    }

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME] ?: ""
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_LOGGED_IN] ?: false
    }

    val hasName: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[HAS_NAME] ?: false
    }

    val serverUrl: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[SERVER_URL] ?: ""
    }

    suspend fun saveLoginData(accessId: String, userId: String) {
        context.dataStore.edit { prefs ->
            prefs[ACCESS_ID] = accessId
            prefs[USER_ID] = userId
            prefs[IS_LOGGED_IN] = true
        }
    }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME] = name
            prefs[HAS_NAME] = true
        }
    }

    suspend fun saveServerUrl(url: String) {
        context.dataStore.edit { prefs ->
            prefs[SERVER_URL] = url
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}

package com.ericg.neatflix.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/** A calm reminder. This class is injected. Do not instantiate it directly.*/
class UserPreferences(private val context: Context) {
    private companion object {
        val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "preferences")
        val INCLUDE_ADULT_KEY = booleanPreferencesKey("include_adult")
        val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        val USER_NAME_KEY = stringPreferencesKey("user_name")
    }

    val includeAdultFlow: Flow<Boolean?> = context.dataStore.data.map { prefs ->
        prefs[INCLUDE_ADULT_KEY] ?: true
    }
    suspend fun updateIncludeAdult(includeAdult: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[INCLUDE_ADULT_KEY] = includeAdult
        }
    }

    val authTokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[AUTH_TOKEN_KEY]
    }

    val userNameFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[USER_NAME_KEY]
    }

    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { prefs ->
            prefs[AUTH_TOKEN_KEY] = token
        }
    }

    suspend fun saveUserName(name: String) {
        context.dataStore.edit { prefs ->
            prefs[USER_NAME_KEY] = name
        }
    }

    suspend fun clearAuthToken() {
        context.dataStore.edit { prefs ->
            prefs.remove(AUTH_TOKEN_KEY)
            prefs.remove(USER_NAME_KEY)
        }
    }
}
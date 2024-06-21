package com.example.fleettrack.data

import android.content.ContentValues
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import com.example.fleettrack.model.UserCredentials
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class PreferencesRepository(
    private val dataStore: DataStore<Preferences>
) {
    private companion object {
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val USER_ID = stringPreferencesKey("user_id")
        val PASSWORD = stringPreferencesKey("pass")
    }

    suspend fun saveLoggedIn(
        isLoggedIn: Boolean,
        userCredentials: UserCredentials
    ) {
        dataStore.edit { preferences ->
            preferences[IS_LOGGED_IN] = isLoggedIn
            preferences[USER_ID] = userCredentials.userid
            preferences[PASSWORD] = userCredentials.password
        }
    }

    val isLoggedIn: Flow<Boolean> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(ContentValues.TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            preferences[IS_LOGGED_IN] ?: true
        }

    val userCredentials: Flow<UserCredentials> = dataStore.data
        .catch {
            if(it is IOException) {
                Log.e(ContentValues.TAG, "Error reading preferences.", it)
                emit(emptyPreferences())
            } else {
                throw it
            }
        }
        .map { preferences ->
            UserCredentials(
                userid = preferences[USER_ID] ?: "",
                password = preferences[PASSWORD] ?: ""
            )
        }
}
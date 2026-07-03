package com.example.tsenantsika.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.tsenantsika.data.entities.Role
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("session")

class SessionManager(private val context: Context) {

    private val userIdKey = longPreferencesKey("user_id")
    private val userNameKey = stringPreferencesKey("user_name")
    private val userRoleKey = stringPreferencesKey("user_role")

    val sessionFlow: Flow<Session?> = context.dataStore.data.map { prefs ->
        val id = prefs[userIdKey] ?: return@map null
        val name = prefs[userNameKey] ?: return@map null
        val role = prefs[userRoleKey]?.let { Role.valueOf(it) } ?: return@map null
        Session(id, name, role)
    }

    suspend fun saveSession(id: Long, name: String, role: Role) {
        context.dataStore.edit { prefs ->
            prefs[userIdKey] = id
            prefs[userNameKey] = name
            prefs[userRoleKey] = role.name
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}

data class Session(val userId: Long, val userName: String, val role: Role)

package com.fanindo.submissionstoryapp.model

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {


    suspend fun saveUser(user: UserModel) {
        dataStore.edit { preferences ->
            preferences[NAME] = user.name
            preferences[USER_ID] = user.userId
            preferences[TOKEN] = user.token
            preferences[STATE] = user.isLogin
        }
    }

    fun getUser(): Flow<UserModel> {
        return dataStore.data.map { preferences ->
            UserModel(
                preferences[NAME] ?: "",
                preferences[USER_ID] ?: "",
                preferences[TOKEN] ?: "",
                preferences[STATE] ?: false,
            )
        }
    }

    suspend fun login() {
        dataStore.edit { preferences ->
            preferences[STATE] = true
        }
    }

    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences[STATE] = false
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null
        private val NAME = stringPreferencesKey("name")
        private val USER_ID = stringPreferencesKey("user_id")
        private val TOKEN = stringPreferencesKey("token")
        private val STATE = booleanPreferencesKey("state")

        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }
}
package ru.geowork.photoapp.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    suspend fun savePhotographName(value: String) {
        dataStore.edit { it[PHOTOGRAPH_NAME] = value }
    }

    suspend fun saveSupervisorName(value: String) {
        dataStore.edit { it[SUPERVISOR_NAME] = value }
    }

    suspend fun getPhotographName(): String? = dataStore.data.map { it[PHOTOGRAPH_NAME] }.firstOrNull()
    suspend fun getSupervisorName(): String? = dataStore.data.map { it[SUPERVISOR_NAME] }.firstOrNull()

    companion object {
        private val PHOTOGRAPH_NAME = stringPreferencesKey("photograph_name")
        private val SUPERVISOR_NAME = stringPreferencesKey("supervisor_name")
    }
}

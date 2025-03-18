package ru.geowork.photoapp.data

import androidx.camera.core.ImageCapture
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreRepository @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    //Account
    suspend fun getPhotographName(): String? = dataStore.data.map { it[PHOTOGRAPH_NAME] }.firstOrNull()
    suspend fun getSupervisorName(): String? = dataStore.data.map { it[SUPERVISOR_NAME] }.firstOrNull()

    suspend fun savePhotographName(value: String) { dataStore.edit { it[PHOTOGRAPH_NAME] = value } }
    suspend fun saveSupervisorName(value: String) { dataStore.edit { it[SUPERVISOR_NAME] = value } }

    //Mode
    suspend fun getCollectionMode(): Boolean = dataStore.data.map { it[COLLECTION_MODE] }.firstOrNull() ?: false
    suspend fun saveCollectionMode(value: Boolean) { dataStore.edit { it[COLLECTION_MODE] = value } }

    //Camera
    suspend fun getCameraShowGrid(): Boolean? = dataStore.data.map { it[CAMERA_SHOW_GRID] }.firstOrNull()
    suspend fun getCameraExposureCompensationIndex(): Int? = dataStore.data.map { it[CAMERA_EXPOSURE_COMPENSATION_INDEX] }.firstOrNull()
    suspend fun getCameraZoom(): Float? = dataStore.data.map { it[CAMERA_ZOOM] }.firstOrNull()

    suspend fun saveCameraShowGrid(value: Boolean) { dataStore.edit { it[CAMERA_SHOW_GRID] = value } }
    suspend fun saveCameraExposure(value: Int) { dataStore.edit { it[CAMERA_EXPOSURE_COMPENSATION_INDEX] = value } }
    suspend fun saveCameraZoom(value: Float) { dataStore.edit { it[CAMERA_ZOOM] = value } }

    suspend fun getMaxImageSize(): Int = dataStore.data.map { it[MAX_IMAGE_SIZE] }.firstOrNull() ?: 4096
    suspend fun saveMaxImageSize(value: Int) { dataStore.edit { it[MAX_IMAGE_SIZE] = value } }

    suspend fun getCaptureMode(): Int = dataStore.data.map { it[CAMERA_CAPTURE_MODE] }.firstOrNull() ?: ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY
    suspend fun saveCaptureMode(value: Int) { dataStore.edit { it[CAMERA_CAPTURE_MODE] = value } }

    companion object {
        private val PHOTOGRAPH_NAME = stringPreferencesKey("photograph_name")
        private val SUPERVISOR_NAME = stringPreferencesKey("supervisor_name")

        private val COLLECTION_MODE = booleanPreferencesKey("collection_mode")

        private val CAMERA_SHOW_GRID = booleanPreferencesKey("camera_show_grid")
        private val CAMERA_EXPOSURE_COMPENSATION_INDEX = intPreferencesKey("camera_exposure_compensation_index")
        private val CAMERA_ZOOM = floatPreferencesKey("camera_zoom")
        private val CAMERA_CAPTURE_MODE = intPreferencesKey("camera_capture_mode")

        private val MAX_IMAGE_SIZE = intPreferencesKey("image_size")
    }
}

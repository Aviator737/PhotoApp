package ru.geowork.photoapp.ui.screen.camera

import androidx.camera.core.SurfaceRequest
import ru.geowork.photoapp.ui.base.UiState

data class CameraUiState(
    val surfaceRequest: SurfaceRequest? = null
): UiState

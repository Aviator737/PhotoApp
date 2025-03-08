package ru.geowork.photoapp.ui.screen.camera

import android.graphics.Bitmap
import ru.geowork.photoapp.ui.base.UiAction

sealed class CameraUiAction: UiAction {
    data class OnZoomLevelsResolved(
        val minZoom: Float,
        val maxZoom: Float
    ): CameraUiAction()
    data class OnExposureResolved(
        val isSupported: Boolean,
        val default: Int,
        val step: Float,
        val min: Int,
        val max: Int
    ): CameraUiAction()
    data class OnExposureStepSelected(val step: CameraUiState.ExposureState.Step): CameraUiAction()
    data class OnZoomSelected(val value: Float): CameraUiAction()
    data object SwitchGrid: CameraUiAction()
    data object SwitchExposureMenu: CameraUiAction()
    data class OnPhotoTaken(val bitmap: Bitmap): CameraUiAction()
    data object NavigateBack: CameraUiAction()
}

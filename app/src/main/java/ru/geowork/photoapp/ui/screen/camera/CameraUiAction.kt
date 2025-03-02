package ru.geowork.photoapp.ui.screen.camera

import ru.geowork.photoapp.ui.base.UiAction

sealed class CameraUiAction: UiAction {
    data object SwitchHDR: CameraUiAction()
    data class OnZoomLevelsResolved(
        val minZoom: Float,
        val maxZoom: Float
    ): CameraUiAction()
    data class OnZoomSelected(val value: Float): CameraUiAction()
    data object SwitchGrid: CameraUiAction()
    data object SwitchExposureMenu: CameraUiAction()
    data object NavigateBack: CameraUiAction()
}

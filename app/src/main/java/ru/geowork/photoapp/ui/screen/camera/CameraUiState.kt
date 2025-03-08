package ru.geowork.photoapp.ui.screen.camera

import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.base.UiState
import kotlin.math.abs

data class CameraUiState(
    val imageQuality: Int? = null,

    val exposureState: ExposureState = ExposureState(),

    val zoomLevels: List<Pair<Float, Boolean>> = listOf(),
    val showGrid: Boolean = false,
    val items: List<FolderItem> = listOf()
): UiState {

    data class ExposureState(
        val isVisible: Boolean = false,
        val isSupported: Boolean = false,
        val selectedStep: Step? = null,
        val steps: List<Step> = listOf(),
        val isExposureSettingsVisible: Boolean = false,
        val exposureStep: Int? = null,
    ) {
        data class Step(
            val index: Int,
            val value: Float
        ) {
            companion object {
                fun List<Step>.findClosestElement(targetValue: Float): Step? = minByOrNull { abs(it.value - targetValue) }
            }
        }
    }

}

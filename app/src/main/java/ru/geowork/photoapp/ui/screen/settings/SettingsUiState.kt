package ru.geowork.photoapp.ui.screen.settings

import ru.geowork.photoapp.ui.base.UiState

data class SettingsUiState(
    val maxImageSize: String? = null,
    val captureModeState: CaptureModeState = CaptureModeState()
): UiState {

    data class CaptureModeState(
        val captureMode: Int? = null,
        val chooser: Chooser? = null
    ) {
        data class Chooser(
            val options: List<Int> = listOf(),
            val selected: Int? = null
        )
    }
}

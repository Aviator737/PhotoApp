package ru.geowork.photoapp.ui.screen.settings

import ru.geowork.photoapp.ui.base.UiAction

sealed class SettingsUiAction: UiAction {
    data class OnImageMaxSizeInput(val value: String): SettingsUiAction()

    data object OnCaptureModeOpenChooser: SettingsUiAction()
    data class OnCaptureModeSelected(val value: Int): SettingsUiAction()
    data object OnCaptureModeSelectorDismiss: SettingsUiAction()
    data object OnCaptureModeSelectorConfirm: SettingsUiAction()

    data object OnDeleteSyncStateStoreClick: SettingsUiAction()
}

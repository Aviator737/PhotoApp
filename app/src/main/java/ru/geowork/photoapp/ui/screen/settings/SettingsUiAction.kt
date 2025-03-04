package ru.geowork.photoapp.ui.screen.settings

import ru.geowork.photoapp.ui.base.UiAction

sealed class SettingsUiAction: UiAction {
    data class OnImageQualityInput(val value: String): SettingsUiAction()
}

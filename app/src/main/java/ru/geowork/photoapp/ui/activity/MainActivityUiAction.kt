package ru.geowork.photoapp.ui.activity

import ru.geowork.photoapp.ui.base.UiAction

sealed class MainActivityUiAction : UiAction {
    data object OnFilePermissionsNotGranted: MainActivityUiAction()
    data object CloseAlerts: MainActivityUiAction()
}

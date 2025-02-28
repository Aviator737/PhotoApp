package ru.geowork.photoapp.ui.screen.camera

import ru.geowork.photoapp.ui.base.UiAction

sealed class CameraUiAction: UiAction {
    data object NavigateBack: CameraUiAction()
}

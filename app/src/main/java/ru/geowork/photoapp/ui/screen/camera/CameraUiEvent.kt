package ru.geowork.photoapp.ui.screen.camera

import ru.geowork.photoapp.ui.base.UiEvent

sealed class CameraUiEvent: UiEvent() {
    data object NavigateBack: CameraUiEvent()
}

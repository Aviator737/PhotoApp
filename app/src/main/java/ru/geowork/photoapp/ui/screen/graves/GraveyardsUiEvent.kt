package ru.geowork.photoapp.ui.screen.graves

import ru.geowork.photoapp.ui.base.UiEvent
import ru.geowork.photoapp.ui.screen.camera.CameraPayload

sealed class GraveyardsUiEvent: UiEvent() {
    data object NavigateBack: GraveyardsUiEvent()
    data class NavigateToCamera(val payload: CameraPayload): GraveyardsUiEvent()
}

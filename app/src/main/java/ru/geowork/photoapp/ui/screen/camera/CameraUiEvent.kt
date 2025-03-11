package ru.geowork.photoapp.ui.screen.camera

import ru.geowork.photoapp.ui.base.UiEvent
import ru.geowork.photoapp.ui.screen.gallery.GalleryPayload

sealed class CameraUiEvent: UiEvent() {
    data class NavigateToGallery(val payload: GalleryPayload): CameraUiEvent()
    data object NavigateBack: CameraUiEvent()
}

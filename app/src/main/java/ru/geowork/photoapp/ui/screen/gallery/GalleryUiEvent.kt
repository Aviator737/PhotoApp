package ru.geowork.photoapp.ui.screen.gallery

import ru.geowork.photoapp.ui.base.UiEvent

sealed class GalleryUiEvent: UiEvent() {
    data object NavigateBack: GalleryUiEvent()
}

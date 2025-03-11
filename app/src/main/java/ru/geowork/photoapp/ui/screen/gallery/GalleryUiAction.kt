package ru.geowork.photoapp.ui.screen.gallery

import ru.geowork.photoapp.ui.base.UiAction

sealed class GalleryUiAction: UiAction {
    data class OnPageChanged(val position: Int): GalleryUiAction()

    data object OnDeleteClick: GalleryUiAction()
    data object OnCloseClick: GalleryUiAction()

    data object OnDeleteCancel: GalleryUiAction()
    data object OnDeleteConfirm: GalleryUiAction()
}

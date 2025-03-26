package ru.geowork.photoapp.ui.screen.gallery

import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.base.UiState

data class GalleryUiState(
    val isReadOnly: Boolean = false,
    val isInitialized: Boolean = false,
    val isDeleteConfirmDialogShowing: Boolean = false,
    val currentItem: Int = 0,
    val items: List<FolderItem.ImageFile> = listOf()
): UiState

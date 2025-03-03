package ru.geowork.photoapp.ui.screen.camera

import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.base.UiState

data class CameraUiState(
    val isHdrOn: Boolean = false,
    val exposureCompensationIndex: Int? = null,
    val zoomLevels: List<Pair<Float, Boolean>> = listOf(),
    val showGrid: Boolean = false,
    val items: List<FolderItem> = listOf()
): UiState

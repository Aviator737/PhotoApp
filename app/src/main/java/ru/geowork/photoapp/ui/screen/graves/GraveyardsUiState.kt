package ru.geowork.photoapp.ui.screen.graves

import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.base.UiState

data class GraveyardsUiState(
    val showBackButton: Boolean = true,
    val showOptionsButton: Boolean = true,
    val isEditMode: Boolean = false,

    val folderLevel: FolderLevel,
    val parentFolders: List<String> = listOf(),
    val folderItems: List<FolderItem> = listOf(),

    val newItemDialog: FolderItem? = null
): UiState

package ru.geowork.photoapp.ui.screen.graves

import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.base.UiState

data class GraveyardsUiState(
    val showBackButton: Boolean = true,
    val showOptionsButton: Boolean = false,
    val isEditMode: Boolean = false,

    val folderLevel: FolderLevel,
    val parentFolders: List<FolderItem.Folder> = listOf(),
    val folderItems: List<FolderItem> = listOf(),

    val newItemDialog: FolderItem? = null
): UiState

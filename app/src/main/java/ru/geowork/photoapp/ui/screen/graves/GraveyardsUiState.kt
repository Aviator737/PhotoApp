package ru.geowork.photoapp.ui.screen.graves

import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.base.UiState

data class GraveyardsUiState(
    val showBackButton: Boolean = true,
    val showOptionsButton: Boolean = false,
    val isEditMode: Boolean = false,

    val folderLevel: FolderLevel = FolderLevel.GRAVEYARDS,
    val parentFolders: List<FolderItem.Folder> = listOf(),
    val folderItems: List<FolderItem> = listOf(),

    val newItemDialog: NewFolderItemDialogState? = null,
    val deleteConfirmationDialog: DeleteConfirmationDialogState? = null,
    val optionsDialog: Boolean = false
): UiState {
    data class NewFolderItemDialogState(
        val item: FolderItem,
        val focusIndex: Int
    )

    data class DeleteConfirmationDialogState(
        val name: String,
        val isLoading: Boolean = false
    )
}

package ru.geowork.photoapp.ui.screen.graves

import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.base.UiState

data class GraveyardsUiState(
    val showBackButton: Boolean = true,
    val showOptionsButton: Boolean = false,
    val isEditMode: Boolean = false,

    val parentFolders: List<FolderItem.Folder> = listOf(),
    val folderItems: List<FolderItem> = listOf(),

    val newFolderDialog: NewFolderDialogState? = null,
    val photoRowDocumentDialog: PhotoRowDocumentDialog? = null,
    val deleteConfirmationDialog: DeleteConfirmationDialogState? = null,
    val optionsDialog: Boolean = false
): UiState {

    data class NewFolderDialogState(
        val item: FolderItem.Folder,
        val focusIndex: Int,
        val isEditMode: Boolean,
        val showEditModeCheckbox: Boolean
    )

    data class PhotoRowDocumentDialog(
        val item: FolderItem.DocumentFile,
        val parent: FolderItem.Folder
    )

    data class DeleteConfirmationDialogState(
        val name: String,
        val isLoading: Boolean = false
    )
}

package ru.geowork.photoapp.ui.screen.graves

import android.net.Uri
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.base.UiAction

sealed class GraveyardsUiAction: UiAction {
    data class SetShowBackButton(val value: Boolean): GraveyardsUiAction()
    data class SetShowOptionsButton(val value: Boolean): GraveyardsUiAction()
    data class SetIsEditMode(val value: Boolean): GraveyardsUiAction()
    data object OnBack: GraveyardsUiAction()

    data object OnUpdateFolderItems: GraveyardsUiAction()

    data class OnAddExternalFile(val uri: Uri, val fileName: String): GraveyardsUiAction()

    data class OnParentFolderClick(val item: FolderItem.Folder): GraveyardsUiAction()
    data class OnFolderItemClick(val item: FolderItem): GraveyardsUiAction()
    data class OnChildItemClick(val parent: FolderItem.Folder, val child: FolderItem): GraveyardsUiAction()

    data object OnAddFolderClick: GraveyardsUiAction()
    data object OnAddTextFileClick: GraveyardsUiAction()
    data class OnItemNameInput(val name: String): GraveyardsUiAction()
    data object OnDismissItemDialog: GraveyardsUiAction()
    data object OnItemNameConfirm: GraveyardsUiAction()

    data class OnTakePhotoClick(val folder: FolderItem.Folder): GraveyardsUiAction()
}

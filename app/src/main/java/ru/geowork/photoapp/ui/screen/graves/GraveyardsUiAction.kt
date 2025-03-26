package ru.geowork.photoapp.ui.screen.graves

import android.net.Uri
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.base.UiAction

sealed class GraveyardsUiAction: UiAction {
    data class SetShowBackButton(val value: Boolean): GraveyardsUiAction()
    data class SetIsEditMode(val value: Boolean): GraveyardsUiAction()
    data object OnBack: GraveyardsUiAction()

    data object OnUpdateFolderItems: GraveyardsUiAction()

    data object OnSyncBarButtonClick: GraveyardsUiAction()

    data class OnAddExternalFile(val uri: Uri, val fileName: String): GraveyardsUiAction()

    data class OnParentFolderClick(val item: FolderItem.Folder): GraveyardsUiAction()
    data class OnFolderItemClick(val item: FolderItem): GraveyardsUiAction()
    data class OnPhotoRowPhotoClick(val parent: FolderItem.Folder, val image: FolderItem.ImageFile): GraveyardsUiAction()

    data class OnPhotoRowDocumentClick(val parent: FolderItem.Folder): GraveyardsUiAction()
    data class OnPhotoRowDocumentDialogTextInput(val text: String): GraveyardsUiAction()
    data object OnPhotoRowDocumentDialogDismiss: GraveyardsUiAction()
    data object OnPhotoRowDocumentDialogConfirm: GraveyardsUiAction()

    data class OnAddFolderClick(val prefix: String, val postfix: String): GraveyardsUiAction()
    data class OnEditModeCheckboxClick(val enabled: Boolean, val editModePostfix: String): GraveyardsUiAction()
    data class OnItemNameInput(val name: String, val editModePostfix: String): GraveyardsUiAction()
    data object OnDismissItemDialog: GraveyardsUiAction()
    data object OnItemNameConfirm: GraveyardsUiAction()

    data class OnTakePhotoClick(val folder: FolderItem.Folder): GraveyardsUiAction()

    data object OnOptionsClick: GraveyardsUiAction()
    data object OnOptionsDismiss: GraveyardsUiAction()

    data object OnContinueWorkClick: GraveyardsUiAction()
    data object OnFinishWorkClick: GraveyardsUiAction()
    data object OnDeleteRequestClick: GraveyardsUiAction()
    data object OnDeleteDismissClick: GraveyardsUiAction()
    data object OnDeleteConfirmedClick: GraveyardsUiAction()
}

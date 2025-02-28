package ru.geowork.photoapp.ui.screen.graves

import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.base.UiAction

sealed class GraveyardsUiAction: UiAction {
    data class SetShowBackButton(val value: Boolean): GraveyardsUiAction()
    data class SetShowOptionsButton(val value: Boolean): GraveyardsUiAction()
    data class SetIsEditMode(val value: Boolean): GraveyardsUiAction()
    data object OnBack: GraveyardsUiAction()

    data class OnParentFolderClick(val item: String): GraveyardsUiAction()
    data class OnFolderItemClick(val item: FolderItem): GraveyardsUiAction()

    data class SetShowBottomSheet(val value: Boolean): GraveyardsUiAction()
    data object OnAddFolderClick: GraveyardsUiAction()
    data object OnAddImageFileClick: GraveyardsUiAction()
    data object OnAddTextFileClick: GraveyardsUiAction()
    data class OnItemNameInput(val name: String): GraveyardsUiAction()
    data object OnDismissItemDialog: GraveyardsUiAction()
    data object OnItemNameConfirm: GraveyardsUiAction()

    data class OnTakePhotoClick(val photoRow: FolderItem.PhotoRow): GraveyardsUiAction()
    data object OnRepeatPhoto: GraveyardsUiAction()
    data object OnStopTakePhotos: GraveyardsUiAction()
}

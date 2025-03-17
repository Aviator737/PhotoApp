package ru.geowork.photoapp.ui.screen.graves

import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.base.UiEvent
import ru.geowork.photoapp.ui.screen.camera.CameraPayload
import ru.geowork.photoapp.ui.screen.gallery.GalleryPayload
import ru.geowork.photoapp.ui.screen.upload.UploadPayload

sealed class GraveyardsUiEvent: UiEvent() {
    data object NavigateBack: GraveyardsUiEvent()
    data class NavigateToCamera(val payload: CameraPayload): GraveyardsUiEvent()
    data class OpenInExternalApp(val item: FolderItem.DocumentFile): GraveyardsUiEvent()
    data class NavigateToGallery(val payload: GalleryPayload): GraveyardsUiEvent()
    data class NavigateToUpload(val payload: UploadPayload): GraveyardsUiEvent()
}

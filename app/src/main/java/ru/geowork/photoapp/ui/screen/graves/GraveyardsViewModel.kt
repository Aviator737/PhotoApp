package ru.geowork.photoapp.ui.screen.graves

import android.net.Uri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.geowork.photoapp.data.DataStoreRepository
import ru.geowork.photoapp.data.FilesRepository
import ru.geowork.photoapp.data.GraveyardsRepository
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.base.BaseViewModel
import ru.geowork.photoapp.ui.screen.camera.CameraPayload
import ru.geowork.photoapp.ui.screen.gallery.GalleryPayload
import ru.geowork.photoapp.ui.screen.upload.UploadPayload
import ru.geowork.photoapp.util.SEPARATOR
import javax.inject.Inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@HiltViewModel
class GraveyardsViewModel @Inject constructor(
    private val graveyardsRepository: GraveyardsRepository,
    private val filesRepository: FilesRepository,
    private val dataStoreRepository: DataStoreRepository
): BaseViewModel<GraveyardsUiState, GraveyardsUiEvent, GraveyardsUiAction>() {

    private val parentFolders
        get() = uiState.value.parentFolders

    override val initialUiState: GraveyardsUiState = GraveyardsUiState()

    init {
        initEditMode()
    }

    override fun handleCoroutineException(e: Throwable) {
        println(e)
    }

    override fun onUiAction(uiAction: GraveyardsUiAction) {
        when(uiAction) {
            is GraveyardsUiAction.SetIsEditMode -> handleSetIsEditMode(uiAction.value)
            is GraveyardsUiAction.SetShowBackButton -> handleSetShowBackButton(uiAction.value)
            GraveyardsUiAction.OnBack -> handleBack()

            GraveyardsUiAction.OnUpdateFolderItems -> updateFolderItems()

            is GraveyardsUiAction.OnAddExternalFile -> handleOnAddExternalFile(uiAction.uri, uiAction.fileName)

            is GraveyardsUiAction.OnParentFolderClick -> handleOnParentFolderClick(uiAction.item)
            is GraveyardsUiAction.OnFolderItemClick -> handleOnFolderItemClick(uiAction.item)
            is GraveyardsUiAction.OnPhotoRowPhotoClick -> handleOnPhotoRowPhotoClick(uiAction.parent, uiAction.image)

            is GraveyardsUiAction.OnPhotoRowDocumentClick -> handleOnPhotoRowDocumentClick(uiAction.parent, uiAction.document)
            is GraveyardsUiAction.OnPhotoRowDocumentDialogTextInput -> handleOnPhotoRowDocumentDialogTextInput(uiAction.text)
            GraveyardsUiAction.OnPhotoRowDocumentDialogConfirm -> handleOnPhotoRowDocumentDialogConfirm()
            GraveyardsUiAction.OnPhotoRowDocumentDialogDismiss -> handleOnPhotoRowDocumentDialogDismiss()

            is GraveyardsUiAction.OnAddFolderClick -> handleOnAddFolderClick(uiAction.prefix, uiAction.postfix)
            is GraveyardsUiAction.OnEditModeCheckboxClick -> handleOnEditModeCheckboxClick(uiAction.enabled, uiAction.editModePostfix)
            is GraveyardsUiAction.OnItemNameInput -> handleOnItemNameInput(uiAction.name, uiAction.editModePostfix)
            GraveyardsUiAction.OnDismissItemDialog -> handleOnDismissItemDialog()
            GraveyardsUiAction.OnItemNameConfirm -> handleOnItemNameConfirm()

            is GraveyardsUiAction.OnTakePhotoClick -> handleOnTakePhotoClick(uiAction.folder)

            GraveyardsUiAction.OnOptionsClick -> handleOnOptionsClick()
            GraveyardsUiAction.OnOptionsDismiss -> handleOnOptionsDismiss()

            GraveyardsUiAction.OnNavigateToUploadClick -> handleOnNavigateToUploadClick()
            GraveyardsUiAction.OnDeleteRequestClick -> handleOnDeleteRequestClick()
            GraveyardsUiAction.OnDeleteDismissClick -> handleOnDeleteDismissClick()
            GraveyardsUiAction.OnDeleteConfirmedClick -> handleOnDeleteConfirmedClick()
        }
    }

    private fun updateFolderItems() = viewModelScopeErrorHandled.launch {
        val folderItems = getFolderItems()
        updateUiState { it.copy(folderItems = folderItems) }
    }

    private fun handleOnAddExternalFile(
        sourceUri: Uri,
        fileName: String
    ) = viewModelScopeErrorHandled.launch {
        filesRepository.copyFromUri(sourceUri, fileName, getPath())
        updateFolderItems()
    }

    private fun initEditMode() = viewModelScopeErrorHandled.launch {
        val isEditMode = dataStoreRepository.getCollectionMode()
        updateUiState { it.copy(isEditMode = isEditMode) }
    }

    private fun handleSetIsEditMode(value: Boolean) = viewModelScopeErrorHandled.launch {
        dataStoreRepository.saveCollectionMode(value)
        val newParentFolders = if (parentFolders.isNotEmpty()) listOf(parentFolders.first()) else listOf()
        updateUiState {
            it.copy(
                parentFolders = newParentFolders,
                isEditMode = value,
                showOptionsButton = shouldShowOptionsButton(newParentFolders.size)
            )
        }
        updateFolderItems()
    }

    private fun handleSetShowBackButton(value: Boolean) {
        updateUiState { it.copy(showBackButton = value) }
    }

    private fun handleBack() = viewModelScopeErrorHandled.launch {
        when(parentFolders.size) {
            0 -> sendUiEvent(GraveyardsUiEvent.NavigateBack)
            1 -> {
                val folderItems = getRootFolderItems()
                updateUiState {
                    it.copy(
                        parentFolders = listOf(),
                        folderItems = folderItems,
                        showOptionsButton = shouldShowOptionsButton(0)
                    )
                }
            }
            else -> parentFolders.getOrNull(parentFolders.size - 2)?.let {
                handleOnParentFolderClick(it)
            }
        }
    }

    private fun handleOnParentFolderClick(item: FolderItem.Folder) = viewModelScopeErrorHandled.launch {
        val index = parentFolders.indexOf(item)
        if (parentFolders.size-1 != index && index != -1) {
            val newParentFolders = parentFolders.subList(0, index+1)
            val folderItems = getFolderItems(getPath(newParentFolders), newParentFolders.size)
            updateUiState {
                it.copy(
                    parentFolders = newParentFolders,
                    folderItems = folderItems,
                    showOptionsButton = shouldShowOptionsButton(newParentFolders.size)
                )
            }
        }
    }

    private fun handleOnFolderItemClick(item: FolderItem) {
        when(item) {
            is FolderItem.Folder -> handleFolderClick(item)
            is FolderItem.ImageFile -> handleOnPhotoClick(item)
            is FolderItem.DocumentFile -> handleOnDocumentFileClick(item)
        }
    }

    private fun handleOnPhotoRowPhotoClick(parent: FolderItem.Folder, image: FolderItem.ImageFile) {
        val position = parent.childItems?.indexOf(image) ?: -1
        if (position != -1) {
            sendUiEvent(GraveyardsUiEvent.NavigateToGallery(GalleryPayload(position, parent.relativePath)))
        }
    }

    private fun handleOnPhotoRowDocumentClick(parent: FolderItem.Folder, document: FolderItem.DocumentFile?) {
        updateUiState {
            it.copy(photoRowDocumentDialog = GraveyardsUiState.PhotoRowDocumentDialog(
                item = document ?: FolderItem.DocumentFile(
                    name = "заметка",
                    type = FolderItem.DocumentFile.DocumentType.TXT
                ),
                parent = parent
            ))
        }
    }

    private fun handleOnPhotoRowDocumentDialogTextInput(text: String) = updateUiState {
        it.copy(photoRowDocumentDialog = it.photoRowDocumentDialog?.copy(
            item = it.photoRowDocumentDialog.item.copy(text = text)
        ))
    }

    private fun handleOnPhotoRowDocumentDialogDismiss() = updateUiState { it.copy(photoRowDocumentDialog = null) }

    private fun handleOnPhotoRowDocumentDialogConfirm() = viewModelScopeErrorHandled.launch {
        uiState.value.photoRowDocumentDialog?.let { dialog ->
            val text = dialog.item.text ?: return@launch
            val documentUri = dialog.item.uri ?: filesRepository.createFolderItem(dialog.item, dialog.parent.relativePath)
            documentUri?.let { uri -> filesRepository.writeTextToFile(uri, text) }
            val folderItems = uiState.value.folderItems.map { item ->
                if (item == dialog.parent && item is FolderItem.Folder) {
                    item.copy(childItems = filesRepository.getFolderItems(item.relativePath))
                } else item
            }
            updateUiState { state ->
                state.copy(
                    folderItems = folderItems,
                    photoRowDocumentDialog = null
                )
            }
        }
    }

    private fun handleFolderClick(item: FolderItem.Folder) = viewModelScopeErrorHandled.launch {
        val newParentFolders = parentFolders.plusElement(item)
        val folderItems = getFolderItems(getPath(newParentFolders), newParentFolders.size)
        updateUiState {
            it.copy(
                parentFolders = newParentFolders,
                folderItems = folderItems,
                showOptionsButton = shouldShowOptionsButton(newParentFolders.size)
            )
        }
    }

    private fun handleOnAddFolderClick(prefix: String, postfix: String) {
        updateUiState {
            it.copy(newFolderDialog = GraveyardsUiState.NewFolderDialogState(
                item = FolderItem.Folder(
                    name = prefix+postfix,
                    level = parentFolders.size
                ),
                focusIndex = prefix.length,
                isEditMode = false,
                showEditModeCheckbox = true
            ))
        }
    }

    private fun handleOnEditModeCheckboxClick(enabled: Boolean, editModePostfix: String) = updateUiState {
        val dialogFolderItem = it.newFolderDialog?.item
        it.copy(newFolderDialog = when(dialogFolderItem) {
            is FolderItem.Folder -> {
                val name = if (enabled && !dialogFolderItem.name.contains(editModePostfix)) {
                    dialogFolderItem.name.plus(editModePostfix)
                } else {
                    dialogFolderItem.name.replace(editModePostfix, "")
                }
                it.newFolderDialog.copy(
                    item = dialogFolderItem.copy(name = name),
                    focusIndex = name.length,
                    isEditMode = enabled
                )
            }
            else -> null
        })
    }

    private fun handleOnItemNameInput(
        name: String,
        editModePostfix: String
    ) = updateUiState {
        it.copy(newFolderDialog = it.newFolderDialog?.copy(
            item = it.newFolderDialog.item.copy(name = name),
            focusIndex = name.length,
            isEditMode = name.contains(editModePostfix)
        ))
    }

    private fun handleOnDismissItemDialog() = updateUiState { it.copy(newFolderDialog = null) }

    private fun handleOnItemNameConfirm() = viewModelScopeErrorHandled.launch {
        uiState.value.newFolderDialog?.item?.let {
            filesRepository.createFolderItem(
                folderItem = it.copy(name = it.name.trim()),
                relativePath = getPath()
            )
        }
        val folderItems = getFolderItems()
        updateUiState {
            it.copy(
                folderItems = folderItems,
                newFolderDialog = null
            )
        }
    }

    private fun handleOnTakePhotoClick(folder: FolderItem.Folder) {
        val graveyardShortName = parentFolders.firstOrNull()?.let { graveyardsRepository.getGraveyardPrefix(it.name) }

        val blockShortName = parentFolders.getOrNull(1)?.name
            ?.lowercase()
            ?.replace("квартал_", "")
            ?.replace("_корректировки", "korr")

        val rowShortName = folder.name
            .lowercase()
            .replace("ряд_", "")
            .replace("_корректировки", "_korr")

        val name = "${graveyardShortName}_${blockShortName}_$rowShortName"
        sendUiEvent(GraveyardsUiEvent.NavigateToCamera(CameraPayload(name, folder.relativePath)))
    }

    private fun handleOnPhotoClick(item: FolderItem.ImageFile) {
        val position = uiState.value.folderItems.filterIsInstance<FolderItem.ImageFile>().indexOf(item)
        if (position != -1) {
            sendUiEvent(GraveyardsUiEvent.NavigateToGallery(GalleryPayload(position, item.parentFolder)))
        }
    }

    private fun handleOnDocumentFileClick(item: FolderItem.DocumentFile) {
        sendUiEvent(GraveyardsUiEvent.OpenInExternalApp(item))
    }

    private fun handleOnOptionsClick() = updateUiState { it.copy(optionsDialog = true) }

    private fun handleOnOptionsDismiss() = updateUiState { it.copy(optionsDialog = false) }

    private fun handleOnNavigateToUploadClick() {
        updateUiState { it.copy(optionsDialog = false) }
        sendUiEvent(GraveyardsUiEvent.NavigateToUpload(UploadPayload(uiState.value.parentFolders.last().relativePath)))
    }

    private fun handleOnDeleteRequestClick() = updateUiState {
        it.copy(deleteConfirmationDialog = GraveyardsUiState.DeleteConfirmationDialogState(
            name = parentFolders.last().name
        ))
    }

    private fun handleOnDeleteDismissClick() = updateUiState { it.copy(deleteConfirmationDialog = null) }

    private fun handleOnDeleteConfirmedClick() = viewModelScopeErrorHandled.launch {
        parentFolders.lastOrNull()?.let { folder ->
            updateUiState { it.copy(deleteConfirmationDialog = it.deleteConfirmationDialog?.copy(isLoading = true)) }
            filesRepository.deleteFolder(folder)
            updateUiState { it.copy(optionsDialog = false, deleteConfirmationDialog = null) }
            handleBack()
        }
    }

    private suspend fun getFolderItems(): List<FolderItem> = getFolderItems(getPath(), uiState.value.parentFolders.size)

    private suspend fun getFolderItems(path: String, folderLevel: Int) = when(folderLevel) {
        0 -> getRootFolderItems()
        2 -> filesRepository.getFolderItems(path).map { item ->
            if (item is FolderItem.Folder) {
                val rowPath = "$path$SEPARATOR${item.name}"
                item.copy(childItems = filesRepository.getFolderItems(rowPath))
            } else item
        }
        else -> filesRepository.getFolderItems(path)
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun getRootFolderItems(): List<FolderItem> {
        val rootGraveyards = graveyardsRepository.getGraveyards().map {
            FolderItem.Folder(id = Uuid.random().toString(), name = it.name, level = 0)
        }
        val folderItems = filesRepository.getFolderItems(getPath(listOf()))
        val foldersWithoutDefaults = folderItems.filterNot { root -> rootGraveyards.any { it.name == root.name } }
        return rootGraveyards.plus(foldersWithoutDefaults)
    }

    private fun shouldShowOptionsButton(parentFoldersSize: Int): Boolean = parentFoldersSize > 1

    private fun getPath(parentFoldersIn: List<FolderItem.Folder> = parentFolders): String {
        return parentFoldersIn.joinToString(SEPARATOR) { it.name }
    }
}

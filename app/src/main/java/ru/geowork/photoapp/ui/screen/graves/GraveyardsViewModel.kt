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
            is GraveyardsUiAction.OnChildItemClick -> handleOnChildItemClick(uiAction.parent, uiAction.child)

            is GraveyardsUiAction.OnAddFolderClick -> handleOnAddFolderClick(uiAction.prefix, uiAction.postfix)
            GraveyardsUiAction.OnAddTextFileClick -> handleOnAddTextFileClick()
            is GraveyardsUiAction.OnItemNameInput -> handleOnItemNameInput(uiAction.name)
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
                folderLevel = getFolderLevel(newParentFolders.size),
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
                        folderLevel = getFolderLevel(0),
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
            val newFolderLevel = getFolderLevel(newParentFolders.size)
            val folderItems = getFolderItems(getPath(newParentFolders), newFolderLevel)
            updateUiState {
                it.copy(
                    folderLevel = newFolderLevel,
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

    private fun handleOnChildItemClick(parent: FolderItem.Folder, child: FolderItem) {
        if (child !is FolderItem.ImageFile) return
        val position = parent.childItems?.indexOf(child) ?: -1
        if (position != -1) {
            sendUiEvent(GraveyardsUiEvent.NavigateToGallery(GalleryPayload(position, parent.relativePath)))
        }
    }

    private fun handleFolderClick(item: FolderItem.Folder) = viewModelScopeErrorHandled.launch {
        val newParentFolders = parentFolders.plusElement(item)
        val newFolderLevel = getFolderLevel(newParentFolders.size)
        val folderItems = getFolderItems(getPath(newParentFolders), newFolderLevel)
        updateUiState {
            it.copy(
                folderLevel = newFolderLevel,
                parentFolders = newParentFolders,
                folderItems = folderItems,
                showOptionsButton = shouldShowOptionsButton(newParentFolders.size)
            )
        }
    }

    private fun handleOnAddFolderClick(prefix: String, postfix: String) {
        updateUiState { it.copy(newItemDialog = GraveyardsUiState.NewFolderItemDialogState(
            item = FolderItem.Folder(name = prefix+postfix), focusIndex = prefix.length
        )) }
    }

    private fun handleOnAddTextFileClick() {
        updateUiState { it.copy(newItemDialog = GraveyardsUiState.NewFolderItemDialogState(
            item = FolderItem.DocumentFile(name = "", type = FolderItem.DocumentFile.DocumentType.TXT), focusIndex = 0
        )) }
    }

    private fun handleOnItemNameInput(name: String) = updateUiState {
        val dialogFolderItem = it.newItemDialog?.item
        it.copy(newItemDialog = when(dialogFolderItem) {
            is FolderItem.Folder -> it.newItemDialog.copy(item = dialogFolderItem.copy(name = name), focusIndex = name.length)
            is FolderItem.ImageFile -> it.newItemDialog.copy(item = dialogFolderItem.copy(name = name), focusIndex = name.length)
            is FolderItem.DocumentFile -> it.newItemDialog.copy(item = dialogFolderItem.copy(name = name), focusIndex = name.length)
            else -> null
        })
    }

    private fun handleOnDismissItemDialog() = updateUiState { it.copy(newItemDialog = null) }

    private fun handleOnItemNameConfirm() = viewModelScopeErrorHandled.launch {
        when(val item = uiState.value.newItemDialog?.item) {
            is FolderItem.Folder -> filesRepository.createFolderItem(
                folderItem = item.copy(name = item.name.trim()),
                relativePath = getPath()
            )
            is FolderItem.DocumentFile -> filesRepository.createFolderItem(
                folderItem = item.copy(name = item.name.trim()),
                relativePath = getPath()
            )
            else -> {}
        }
        val folderItems = getFolderItems()
        updateUiState {
            it.copy(
                folderItems = folderItems,
                newItemDialog = null
            )
        }
    }

    private fun handleOnTakePhotoClick(folder: FolderItem.Folder) {
        val graveyardShortName = parentFolders.firstOrNull()?.let { graveyardsRepository.getGraveyardPrefix(it.name) }

        val blockShortName = parentFolders.getOrNull(1)?.name
            ?.lowercase()
            ?.replace("квартал_", "")
            ?.replace("_корректировки", "_korr")

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
        sendUiEvent(
            GraveyardsUiEvent.NavigateToUpload(
                UploadPayload(uiState.value.parentFolders.last().relativePath)
            )
        )
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

    private suspend fun getFolderItems(): List<FolderItem> = getFolderItems(getPath(), uiState.value.folderLevel)

    private suspend fun getFolderItems(path: String, folderLevel: FolderLevel) = when(folderLevel) {
        FolderLevel.GRAVEYARDS -> getRootFolderItems()
        FolderLevel.BLOCKS -> filesRepository.getFolderItems(path)
        FolderLevel.ROWS -> filesRepository.getFolderItems(path).map { item ->
            if (item is FolderItem.Folder) {
                val rowPath = "$path/${item.name}"
                item.copy(childItems = filesRepository.getFolderItems(rowPath))
            } else item
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun getRootFolderItems(): List<FolderItem> {
        val rootGraveyards = graveyardsRepository.getGraveyards().map {
            FolderItem.Folder(id = Uuid.random().toString(), name = it.name)
        }
        val folderItems = filesRepository.getFolderItems(getPath(listOf()))
        val foldersWithoutDefaults = folderItems.filterNot { root -> rootGraveyards.any { it.name == root.name } }
        return rootGraveyards.plus(foldersWithoutDefaults)
    }

    private fun getFolderLevel(parentFoldersSize: Int): FolderLevel = when(parentFoldersSize) {
        0 -> FolderLevel.GRAVEYARDS
        1 -> FolderLevel.BLOCKS
        else -> FolderLevel.ROWS
    }

    private fun shouldShowOptionsButton(parentFoldersSize: Int): Boolean = parentFoldersSize > 1

    private fun getPath(parentFoldersIn: List<FolderItem.Folder> = parentFolders): String {
        return parentFoldersIn.joinToString("/") { it.name }
    }
}

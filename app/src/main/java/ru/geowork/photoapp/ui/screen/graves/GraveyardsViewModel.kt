package ru.geowork.photoapp.ui.screen.graves

import android.net.Uri
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.geowork.photoapp.data.FilesRepository
import ru.geowork.photoapp.data.GraveyardsRepository
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.base.BaseViewModel
import ru.geowork.photoapp.ui.screen.camera.CameraPayload
import ru.geowork.photoapp.ui.screen.gallery.GalleryPayload
import javax.inject.Inject
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@HiltViewModel
class GraveyardsViewModel @Inject constructor(
    private val graveyardsRepository: GraveyardsRepository,
    private val filesRepository: FilesRepository
): BaseViewModel<GraveyardsUiState, GraveyardsUiEvent, GraveyardsUiAction>() {

    private val parentFolders
        get() = uiState.value.parentFolders

    override val initialUiState: GraveyardsUiState = GraveyardsUiState(folderLevel = FolderLevel.GRAVEYARDS)

    override fun handleCoroutineException(e: Throwable) {}

    override fun onUiAction(uiAction: GraveyardsUiAction) {
        when(uiAction) {
            is GraveyardsUiAction.SetIsEditMode -> handleSetIsEditMode(uiAction.value)
            is GraveyardsUiAction.SetShowBackButton -> handleSetShowBackButton(uiAction.value)
            is GraveyardsUiAction.SetShowOptionsButton -> handleShowOptionsButton(uiAction.value)
            GraveyardsUiAction.OnBack -> handleBack()

            GraveyardsUiAction.OnUpdateFolderItems -> updateFolderItems()

            is GraveyardsUiAction.OnAddExternalFile -> handleOnAddExternalFile(uiAction.uri, uiAction.fileName)

            is GraveyardsUiAction.OnParentFolderClick -> handleOnParentFolderClick(uiAction.item)
            is GraveyardsUiAction.OnFolderItemClick -> handleOnFolderItemClick(uiAction.item)
            is GraveyardsUiAction.OnChildItemClick -> handleOnChildItemClick(uiAction.parent, uiAction.child)

            GraveyardsUiAction.OnAddFolderClick -> handleOnAddFolderClick()
            GraveyardsUiAction.OnAddTextFileClick -> handleOnAddTextFileClick()
            is GraveyardsUiAction.OnItemNameInput -> handleOnItemNameInput(uiAction.name)
            GraveyardsUiAction.OnDismissItemDialog -> handleOnDismissItemDialog()
            GraveyardsUiAction.OnItemNameConfirm -> handleOnItemNameConfirm()

            is GraveyardsUiAction.OnTakePhotoClick -> handleOnTakePhotoClick(uiAction.folder)
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
        val path = parentFolders.joinToString("/") { it.name }
        filesRepository.copyFromUri(sourceUri, fileName, path)
        updateFolderItems()
    }

    private fun handleSetIsEditMode(value: Boolean) {
        updateUiState { it.copy(isEditMode = value) }
    }

    private fun handleSetShowBackButton(value: Boolean) {
        updateUiState { it.copy(showBackButton = value) }
    }

    private fun handleShowOptionsButton(value: Boolean) {
        updateUiState { it.copy(showOptionsButton = value) }
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
                        folderLevel = getFolderLevel(0)
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
            val folderItems = getFolderItems(newParentFolders, newFolderLevel)
            updateUiState {
                it.copy(
                    folderLevel = newFolderLevel,
                    parentFolders = newParentFolders,
                    folderItems = folderItems
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
        val folderItems = getFolderItems(newParentFolders, newFolderLevel)
        updateUiState { state ->
            state.copy(
                folderLevel = newFolderLevel,
                parentFolders = newParentFolders,
                folderItems = folderItems
            )
        }
    }

    private fun handleOnAddFolderClick() {
        updateUiState { it.copy(newItemDialog = FolderItem.Folder(name = "")) }
    }

    private fun handleOnAddTextFileClick() {
        updateUiState { it.copy(newItemDialog = FolderItem.DocumentFile(name = "", type = FolderItem.DocumentFile.DocumentType.TXT)) }
    }

    private fun handleOnItemNameInput(name: String) = updateUiState {
        it.copy(newItemDialog = when(it.newItemDialog) {
            is FolderItem.Folder -> it.newItemDialog.copy(name = name)
            is FolderItem.ImageFile -> it.newItemDialog.copy(name = name)
            is FolderItem.DocumentFile -> it.newItemDialog.copy(name = name)
            else -> null
        })
    }

    private fun handleOnDismissItemDialog() = updateUiState { it.copy(newItemDialog = null) }

    private fun handleOnItemNameConfirm() = viewModelScopeErrorHandled.launch {
        val path = parentFolders.joinToString("/") { it.name }
        when(val item = uiState.value.newItemDialog) {
            is FolderItem.Folder -> filesRepository.createFolderItem(item.copy(name = item.name.trim()), relativePath = path)
            is FolderItem.DocumentFile -> filesRepository.createFolderItem(item.copy(name = item.name.trim()), relativePath = path)
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
        sendUiEvent(GraveyardsUiEvent.NavigateToCamera(CameraPayload(folder.relativePath)))
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

    private suspend fun getFolderItems(): List<FolderItem> = getFolderItems(parentFolders, uiState.value.folderLevel)

    private suspend fun getFolderItems(parents: List<FolderItem.Folder>, folderLevel: FolderLevel): List<FolderItem> =
        getFolderItems(parents.joinToString("/") { it.name }, folderLevel)

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
            FolderItem.Folder(id = Uuid.random().toString(), name = it.prefix, visibleName = it.name)
        }
        val folderItems = filesRepository.getFolderItems("")
        val foldersWithoutDefaults = folderItems.filterNot { root -> rootGraveyards.any { it.name == root.name } }
        return rootGraveyards.plus(foldersWithoutDefaults)
    }

    private fun getFolderLevel(parentFoldersSize: Int): FolderLevel = when(parentFoldersSize) {
        0 -> FolderLevel.GRAVEYARDS
        1 -> FolderLevel.BLOCKS
        else -> FolderLevel.ROWS
    }
}

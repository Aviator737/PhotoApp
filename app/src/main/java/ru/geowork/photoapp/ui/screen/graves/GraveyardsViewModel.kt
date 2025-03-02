package ru.geowork.photoapp.ui.screen.graves

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.geowork.photoapp.data.FilesRepository
import ru.geowork.photoapp.data.GraveyardsRepository
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class GraveyardsViewModel @Inject constructor(
    private val graveyardsRepository: GraveyardsRepository,
    private val filesRepository: FilesRepository
): BaseViewModel<GraveyardsUiState, GraveyardsUiEvent, GraveyardsUiAction>() {

    private val parentFolders
        get() = uiState.value.parentFolders

    override val initialUiState: GraveyardsUiState = GraveyardsUiState(folderLevel = FolderLevel.GRAVEYARDS)

    init {
        initRootFolder()
    }

    override fun handleCoroutineException(e: Throwable) {}

    override fun onUiAction(uiAction: GraveyardsUiAction) {
        when(uiAction) {
            is GraveyardsUiAction.SetIsEditMode -> handleSetIsEditMode(uiAction.value)
            is GraveyardsUiAction.SetShowBackButton -> handleSetShowBackButton(uiAction.value)
            is GraveyardsUiAction.SetShowOptionsButton -> handleShowOptionsButton(uiAction.value)
            GraveyardsUiAction.OnBack -> handleBack()

            is GraveyardsUiAction.OnParentFolderClick -> handleOnParentFolderClick(uiAction.item)
            is GraveyardsUiAction.OnFolderItemClick -> handleOnFolderItemClick(uiAction.item)

            GraveyardsUiAction.OnAddFolderClick -> handleOnAddFolderClick()
            GraveyardsUiAction.OnAddImageFileClick -> handleOnAddImageFileClick()
            GraveyardsUiAction.OnAddTextFileClick -> handleOnAddTextFileClick()
            is GraveyardsUiAction.OnItemNameInput -> handleOnItemNameInput(uiAction.name)
            GraveyardsUiAction.OnDismissItemDialog -> handleOnDismissItemDialog()
            GraveyardsUiAction.OnItemNameConfirm -> handleOnItemNameConfirm()

            is GraveyardsUiAction.OnTakePhotoClick -> handleOnTakePhotoClick(uiAction.photoRow)
            GraveyardsUiAction.OnRepeatPhoto -> handleOnRepeatPhoto()
            GraveyardsUiAction.OnStopTakePhotos -> handleOnStopTakePhotos()
        }
    }

    private fun initRootFolder() = viewModelScopeErrorHandled.launch {
        val folderItems = getRootFolderItems()
        updateUiState {
            it.copy(
                folderLevel = FolderLevel.GRAVEYARDS,
                parentFolders = listOf(),
                folderItems = folderItems
            )
        }
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

    private fun handleOnParentFolderClick(item: String) = viewModelScopeErrorHandled.launch {
        val index = parentFolders.indexOf(item)
        if (parentFolders.size-1 != index && index != -1) {
            val newParentFolders = parentFolders.subList(0, index+1)
            val newFolderLevel = getFolderLevel(newParentFolders.size)
            val newFolderItems = if (newFolderLevel == FolderLevel.GRAVEYARDS){
                getRootFolderItems()
            } else {
                filesRepository.getFolderItems(newParentFolders.joinToString("/"))
            }
            updateUiState {
                it.copy(
                    folderLevel = newFolderLevel,
                    parentFolders = newParentFolders,
                    folderItems = newFolderItems.mapFolderItemsIfNeeded(newParentFolders, newFolderLevel)
                )
            }
        }
    }

    private fun handleOnFolderItemClick(item: FolderItem) {
        when(item) {
            is FolderItem.Folder -> handleFolderClick(item)
            is FolderItem.ImageFile -> TODO()
            is FolderItem.TextFile -> TODO()
            is FolderItem.PhotoRow -> TODO()
            is FolderItem.Unknown -> {}
        }
    }

    private fun handleFolderClick(item: FolderItem.Folder) = viewModelScopeErrorHandled.launch {
        val newParentFolders = parentFolders.plusElement(item.name)
        val newFolderLevel = getFolderLevel(newParentFolders.size)
        val newFolderItems = filesRepository.getFolderItems(newParentFolders.joinToString("/"))
        updateUiState { state ->
            state.copy(
                folderLevel = newFolderLevel,
                parentFolders = newParentFolders,
                folderItems = newFolderItems.mapFolderItemsIfNeeded(newParentFolders, newFolderLevel)
            )
        }
    }

    private fun handleOnAddFolderClick() {
        updateUiState { it.copy(newItemDialog = FolderItem.Folder(name = "", path = parentFolders.joinToString("/"))) }
    }

    private fun handleOnAddImageFileClick() {
        updateUiState { it.copy(newItemDialog = FolderItem.ImageFile(name = "", path = parentFolders.joinToString("/"))) }
    }

    private fun handleOnAddTextFileClick() {
        updateUiState { it.copy(newItemDialog = FolderItem.TextFile(name = "", path = parentFolders.joinToString("/"))) }
    }

    private fun handleOnItemNameInput(name: String) = updateUiState {
        it.copy(newItemDialog = when(it.newItemDialog) {
            is FolderItem.Folder -> it.newItemDialog.copy(name = name)
            is FolderItem.ImageFile -> it.newItemDialog.copy(name = name)
            is FolderItem.TextFile -> it.newItemDialog.copy(name = name)
            else -> null
        })
    }

    private fun handleOnDismissItemDialog() = updateUiState { it.copy(newItemDialog = null) }

    private fun handleOnItemNameConfirm() = viewModelScopeErrorHandled.launch {
        when(val item = uiState.value.newItemDialog) {
            is FolderItem.Folder -> filesRepository.createFolderItem(item.copy(name = item.name.trim()))
            is FolderItem.ImageFile -> {}
            is FolderItem.TextFile -> {}
            else -> {}
        }
        val folderItems = filesRepository.getFolderItems(parentFolders.joinToString("/"))
        updateUiState {
            it.copy(
                folderItems = folderItems.mapFolderItemsIfNeeded(parentFolders, uiState.value.folderLevel),
                newItemDialog = null
            )
        }
    }

    private var i = 0
    private var currentPhotoRow: FolderItem.PhotoRow? = null

    private fun handleOnTakePhotoClick(photoRow: FolderItem.PhotoRow) = viewModelScopeErrorHandled.launch {
//        i++
//        currentPhotoRow = photoRow
//        val item = FolderItem.ImageFile(
//            name = parentFolders.joinToString("_") + "_${photoRow.name}" + "_фото$i",
//            path = photoRow.path
//        )
//        val uri = filesRepository.createFolderItem(item)
        sendUiEvent(GraveyardsUiEvent.NavigateToCamera)
    }

    private fun handleOnRepeatPhoto() {
        currentPhotoRow?.let { handleOnTakePhotoClick(it) }
    }

    private fun handleOnStopTakePhotos() {
        currentPhotoRow = null
    }

    private suspend fun getRootFolderItems(): List<FolderItem> {
        val rootGraveyards = graveyardsRepository.getGraveyards().map { FolderItem.Folder(name = it.name) }
        val folderItems = filesRepository.getFolderItems("")
        val foldersWithoutDefaults = folderItems.filterNot { root -> rootGraveyards.any { it.name == root.name } }
        return rootGraveyards.plus(foldersWithoutDefaults)
    }

    private fun getFolderLevel(parentFoldersSize: Int): FolderLevel = when(parentFoldersSize) {
        0 -> FolderLevel.GRAVEYARDS
        1 -> FolderLevel.BLOCKS
        else -> FolderLevel.ROWS
    }

    private fun List<FolderItem>.mapFolderItemsIfNeeded(
        parentFolders: List<String>,
        newFolderLevel: FolderLevel
    ) = if (newFolderLevel == FolderLevel.ROWS) {
        map {
            FolderItem.PhotoRow(
                name = it.name,
                path = parentFolders.joinToString("/") + "/${it.name}"
            )
        }
    } else this
}

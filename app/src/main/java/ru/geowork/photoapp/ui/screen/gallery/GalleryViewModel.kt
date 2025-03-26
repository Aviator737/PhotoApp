package ru.geowork.photoapp.ui.screen.gallery

import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.geowork.photoapp.data.FilesRepository
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.base.BaseViewModel

@HiltViewModel(assistedFactory = GalleryAssistedFactory::class)
class GalleryViewModel @AssistedInject constructor(
    private val filesRepository: FilesRepository,
    @Assisted private val payload: GalleryPayload
): BaseViewModel<GalleryUiState, GalleryUiEvent, GalleryUiAction>() {

    override val initialUiState: GalleryUiState = GalleryUiState(currentItem = payload.position, isReadOnly = payload.isReadOnly)

    init {
        getItems()
    }

    override fun handleCoroutineException(e: Throwable) {}

    override fun onUiAction(uiAction: GalleryUiAction) {
        when(uiAction) {
            is GalleryUiAction.OnPageChanged -> handleOnPageChanged(uiAction.position)

            GalleryUiAction.OnDeleteClick -> handleOnDeleteClick()
            GalleryUiAction.OnCloseClick -> handleOnCloseClick()

            GalleryUiAction.OnDeleteCancel -> handleOnDeleteCancel()
            GalleryUiAction.OnDeleteConfirm -> handleOnDeleteConfirm()
        }
    }

    private fun handleOnPageChanged(position: Int) {
        updateUiState { it.copy(currentItem = position) }
    }

    private fun handleOnDeleteClick() = viewModelScopeErrorHandled.launch {
        updateUiState { it.copy(isDeleteConfirmDialogShowing = true) }
    }

    private fun handleOnCloseClick() {
        sendUiEvent(GalleryUiEvent.NavigateBack)
    }

    private fun handleOnDeleteCancel() {
        updateUiState { it.copy(isDeleteConfirmDialogShowing = false) }
    }

    private fun handleOnDeleteConfirm() = viewModelScopeErrorHandled.launch {
        updateUiState { it.copy(isDeleteConfirmDialogShowing = false) }
        uiState.value.items[uiState.value.currentItem].uri?.let {
            filesRepository.deleteFile(it)
            getItems()
        }
    }

    private fun getItems() = viewModelScopeErrorHandled.launch {
        val items = filesRepository.getFolderItems(payload.path).filterIsInstance<FolderItem.ImageFile>()
        updateUiState { it.copy(isInitialized = true, items = items) }
        if (items.isEmpty()) { sendUiEvent(GalleryUiEvent.NavigateBack) }
    }
}

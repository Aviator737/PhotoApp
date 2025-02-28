package ru.geowork.photoapp.ui.screen.foldersync

import androidx.lifecycle.SavedStateHandle
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.geowork.photoapp.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class FolderSyncViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle
): BaseViewModel<FolderSyncUiState, FolderSyncUiEvent, FolderSyncUiAction>() {

    private val subgroupArgs = FolderSyncArgs(savedStateHandle)

    override val initialUiState = FolderSyncUiState(title = subgroupArgs.id)

    override fun handleCoroutineException(e: Throwable) {

    }

    override fun onUiAction(uiAction: FolderSyncUiAction) {

    }
}

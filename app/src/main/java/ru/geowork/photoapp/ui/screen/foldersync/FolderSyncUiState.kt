package ru.geowork.photoapp.ui.screen.foldersync

import ru.geowork.photoapp.ui.base.UiState

data class FolderSyncUiState(
    val isLoading: Boolean = false,
    val title: String = ""
): UiState

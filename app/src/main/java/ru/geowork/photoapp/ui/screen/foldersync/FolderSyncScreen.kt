package ru.geowork.photoapp.ui.screen.foldersync

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable

private const val FOLDER_ID_ARG = "folder_id"
const val FOLDER_SYNC_SCREEN_ID = "folder_sync_screen"

fun NavGraphBuilder.folderSyncScreen(
    onBack: () -> Unit
) {
    composable("$FOLDER_SYNC_SCREEN_ID/{$FOLDER_ID_ARG}") {
        val viewModel: FolderSyncViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        FolderSync(
            state = uiState,
            onBack = onBack
        )
    }
}

fun NavController.navigateToFolderSyncScreen(
    id: String,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate("$FOLDER_SYNC_SCREEN_ID/$id", builder)
}

internal class FolderSyncArgs(val id: String) {
    constructor(savedStateHandle: SavedStateHandle) :
            this(checkNotNull(savedStateHandle[FOLDER_ID_ARG]) as String)
}

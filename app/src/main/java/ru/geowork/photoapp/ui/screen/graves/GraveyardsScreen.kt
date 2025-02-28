package ru.geowork.photoapp.ui.screen.graves

import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable

const val GRAVEYARDS_SCREEN_ID = "graveyards_screen"

fun NavGraphBuilder.graveyardsScreen(
    onBack: () -> Unit
) {
    composable(GRAVEYARDS_SCREEN_ID) {
        val viewModel: GraveyardsViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val uiEvents by viewModel.uiEvents.collectAsStateWithLifecycle()

        BackHandler(enabled = true, onBack = { viewModel.onUiAction(GraveyardsUiAction.OnBack) })

        val cameraLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.TakePicture(),
            onResult = { success ->
                if (success) {
                    viewModel.onUiAction(GraveyardsUiAction.OnRepeatPhoto)
                } else {
                    viewModel.onUiAction(GraveyardsUiAction.OnStopTakePhotos)
                }
            }
        )

        uiEvents.firstOrNull()?.let { uiEvent ->
            LaunchedEffect(uiEvent) {
                when(uiEvent) {
                    is GraveyardsUiEvent.NavigateToCamera -> cameraLauncher.launch(uiEvent.uri)
                    GraveyardsUiEvent.NavigateBack -> onBack()
                }
            }
            viewModel.onUiEventHandled(uiEvent)
        }

        Graveyards(
            state = uiState,
            onUiAction = { viewModel.onUiAction(it) }
        )
    }
}

fun NavController.navigateToGraveyardsScreen(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(GRAVEYARDS_SCREEN_ID, builder)
}

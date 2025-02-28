package ru.geowork.photoapp.ui.screen.camera

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable


const val CAMERA_SCREEN_ID = "camera_screen"

fun NavGraphBuilder.cameraScreen(
    onBack: () -> Unit
) {

    composable(CAMERA_SCREEN_ID) {
        val viewModel: CameraViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val uiEvents by viewModel.uiEvents.collectAsStateWithLifecycle()

        uiEvents.firstOrNull()?.let { uiEvent ->
            LaunchedEffect(uiEvent) {
                when(uiEvent) {
                    CameraUiEvent.NavigateBack -> onBack()
                }
            }
            viewModel.onUiEventHandled(uiEvent)
        }

        Camera(
            state = uiState,
            onUiAction = { viewModel.onUiAction(it) }
        )
    }
}

fun NavController.navigateToCameraScreen(
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(CAMERA_SCREEN_ID, builder)
}

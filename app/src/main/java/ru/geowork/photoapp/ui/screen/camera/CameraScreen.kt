package ru.geowork.photoapp.ui.screen.camera

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import ru.geowork.photoapp.ui.screen.camera.components.Camera

fun NavGraphBuilder.cameraScreen(
    onBack: () -> Unit
) {
    composable<CameraPayload> { backStackEntry ->
        val payload = backStackEntry.toRoute<CameraPayload>()
        val viewModel = hiltViewModel<CameraViewModel, CameraAssistedFactory> { it.create(payload) }
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val uiEvents by viewModel.uiEvents.collectAsStateWithLifecycle()

        uiEvents.firstOrNull()?.let { event ->
            LaunchedEffect(uiEvents.firstOrNull()) {
                when(event) {
                    CameraUiEvent.NavigateBack -> onBack()
                }
            }
            viewModel.onUiEventHandled(event)
        }

        Camera(
            state = uiState,
            onUiAction = { viewModel.onUiAction(it) }
        )
    }
}

fun NavController.navigateToCameraScreen(
    payload: CameraPayload,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(
        route = payload,
        builder = builder
    )
}

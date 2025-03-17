package ru.geowork.photoapp.ui.screen.upload

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

fun NavGraphBuilder.uploadScreen(
    onBack: () -> Unit
) {
    composable<UploadPayload> { backStackEntry ->
        val payload = backStackEntry.toRoute<UploadPayload>()
        val viewModel = hiltViewModel<UploadViewModel, UploadAssistedFactory> { it.create(payload) }
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val uiEvents by viewModel.uiEvents.collectAsStateWithLifecycle()

        uiEvents.firstOrNull()?.let { event ->
            LaunchedEffect(uiEvents.firstOrNull()) {

            }
            viewModel.onUiEventHandled(event)
        }

        Upload(
            state = uiState,
            onUiAction = { viewModel.onUiAction(it) }
        )
    }
}

fun NavController.navigateToUploadScreen(
    payload: UploadPayload,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(
        route = payload,
        builder = builder
    )
}

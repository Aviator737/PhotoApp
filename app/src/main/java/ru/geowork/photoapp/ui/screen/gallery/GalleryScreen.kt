package ru.geowork.photoapp.ui.screen.gallery

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute

fun NavGraphBuilder.galleryScreen(
    onBack: () -> Unit
) {
    composable<GalleryPayload> { backStackEntry ->
        val payload = backStackEntry.toRoute<GalleryPayload>()
        val viewModel = hiltViewModel<GalleryViewModel, GalleryAssistedFactory> { it.create(payload) }
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val uiEvents by viewModel.uiEvents.collectAsStateWithLifecycle()

        uiEvents.firstOrNull()?.let { event ->
            LaunchedEffect(uiEvents.firstOrNull()) {
                when(event) {
                    GalleryUiEvent.NavigateBack -> onBack()
                }
            }
            viewModel.onUiEventHandled(event)
        }

        Gallery(
            state = uiState,
            onUiAction = { viewModel.onUiAction(it) }
        )
    }
}

fun NavController.navigateToGalleryScreen(
    payload: GalleryPayload,
    builder: NavOptionsBuilder.() -> Unit = {}
) {
    navigate(
        route = payload,
        builder = builder
    )
}

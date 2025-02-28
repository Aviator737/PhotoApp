package ru.geowork.photoapp.ui.screen.auth

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable

const val AUTH_SCREEN_ID = "auth_screen"

fun NavGraphBuilder.authScreen(
    navigateToMainMenu: () -> Unit
) {
    composable(AUTH_SCREEN_ID) {
        val viewModel: AuthViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val uiEvents by viewModel.uiEvents.collectAsStateWithLifecycle()

        uiEvents.firstOrNull()?.let { uiEvent ->
            LaunchedEffect(uiEvent) {
                when(uiEvent) {
                    AuthUiEvent.OpenMainMenu -> navigateToMainMenu()
                }
            }
            viewModel.onUiEventHandled(uiEvent)
        }

        Auth(
            state = uiState,
            onUiAction = { viewModel.onUiAction(it) },
        )
    }
}

fun NavController.navigateToAuthScreen(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(AUTH_SCREEN_ID, builder)
}

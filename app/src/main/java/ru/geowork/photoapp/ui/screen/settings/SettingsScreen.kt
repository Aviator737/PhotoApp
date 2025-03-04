package ru.geowork.photoapp.ui.screen.settings

import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable

const val SETTINGS_SCREEN_ID = "settings_screen"

fun NavGraphBuilder.settingsScreen() {
    composable(SETTINGS_SCREEN_ID) {
        val viewModel: SettingsViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val uiEvents by viewModel.uiEvents.collectAsStateWithLifecycle()

        Settings(
            state = uiState,
            onUiAction = { viewModel.onUiAction(it) }
        )
    }
}

fun NavController.navigateToSettingsScreen(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(SETTINGS_SCREEN_ID, builder)
}

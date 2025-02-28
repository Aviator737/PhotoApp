package ru.geowork.photoapp.ui.screen.mainmenu

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable

const val MAIN_MENU_SCREEN_ID = "main_menu_screen"

fun NavGraphBuilder.mainMenuScreen(
    navigateToGraves: () -> Unit,
    navigateToPoles: () -> Unit,
    navigateToSettings: () -> Unit
) {
    composable(MAIN_MENU_SCREEN_ID) {
        MainMenu(
            navigateToGraves = navigateToGraves,
            navigateToPoles = navigateToPoles,
            navigateToSettings = navigateToSettings
        )
    }
}

fun NavController.navigateToMainMenuScreen(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(MAIN_MENU_SCREEN_ID, builder)
}

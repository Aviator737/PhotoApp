package ru.geowork.photoapp.ui.screen.mainmenu

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import ru.geowork.photoapp.R

const val MAIN_MENU_SCREEN_ID = "main_menu_screen"

fun NavGraphBuilder.mainMenuScreen(
    navigateToGraves: () -> Unit,
    navigateToPoles: () -> Unit,
    navigateToSettings: () -> Unit
) {
    composable(MAIN_MENU_SCREEN_ID) {
        val context = LocalContext.current

        MainMenu(
            navigateToGraves = navigateToGraves,
            navigateToPoles = {
                Toast.makeText(context, context.getString(R.string.poles_unavailable), Toast.LENGTH_SHORT).show()
                navigateToPoles()
            },
            navigateToSettings = navigateToSettings
        )
    }
}

fun NavController.navigateToMainMenuScreen(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(MAIN_MENU_SCREEN_ID, builder)
}

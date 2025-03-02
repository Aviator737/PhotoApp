package ru.geowork.photoapp.ui.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import ru.geowork.photoapp.ui.screen.auth.authScreen
import ru.geowork.photoapp.ui.screen.camera.cameraScreen
import ru.geowork.photoapp.ui.screen.camera.navigateToCameraScreen
import ru.geowork.photoapp.ui.screen.foldersync.folderSyncScreen
import ru.geowork.photoapp.ui.screen.graves.graveyardsScreen
import ru.geowork.photoapp.ui.screen.graves.navigateToGraveyardsScreen
import ru.geowork.photoapp.ui.screen.mainmenu.mainMenuScreen
import ru.geowork.photoapp.ui.screen.mainmenu.navigateToMainMenuScreen

@Composable
fun AppNavigation(
    startDestination: String,
    modifier: Modifier
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination,
        modifier = modifier,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(300)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(300)
            )
        }
    ) {
        authScreen(
            navigateToMainMenu = {
                navController.navigateToMainMenuScreen()
            }
        )
        mainMenuScreen(
            navigateToGraves = {
                navController.navigateToGraveyardsScreen()
            },
            navigateToPoles = {},
            navigateToSettings = {}
        )
        folderSyncScreen(
            onBack = { navController.popBackStack() }
        )
        graveyardsScreen(
            navigateToCamera = { navController.navigateToCameraScreen() },
            onBack = { navController.popBackStack() }
        )
        cameraScreen(
            onBack = { navController.popBackStack() }
        )
    }
}

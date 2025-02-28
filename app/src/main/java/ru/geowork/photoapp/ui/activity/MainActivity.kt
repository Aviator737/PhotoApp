package ru.geowork.photoapp.ui.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dagger.hilt.android.AndroidEntryPoint
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.components.AppDialog
import ru.geowork.photoapp.ui.navigation.AppNavigation
import ru.geowork.photoapp.ui.screen.auth.AUTH_SCREEN_ID
import ru.geowork.photoapp.ui.screen.mainmenu.MAIN_MENU_SCREEN_ID
import ru.geowork.photoapp.ui.theme.AppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainActivityViewModel by viewModels()

    private val requestAllFilesAccess =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (!Environment.isExternalStorageManager()) {
                viewModel.onUiAction(MainActivityUiAction.OnFilePermissionsNotGranted)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()

            uiState.authorized?.let { authorized ->
                AppTheme {
                    if (uiState.filesPermissionsNotGrantedAlertIsShowing) {
                        AppDialog(
                            dismissButtonText = stringResource(R.string.permissions_not_granted_exit),
                            confirmButtonText = stringResource(R.string.permissions_not_granted_retry),
                            title = stringResource(R.string.permissions_not_granted_title),
                            onDismiss = { finishAffinity() },
                            onConfirm = {
                                viewModel.onUiAction(MainActivityUiAction.CloseAlerts)
                                requestAllFilesPermissions()
                            }
                        ) {
                            Text(
                                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                                text = stringResource(R.string.files_permissions_not_granted_text)
                            )
                        }
                    }
                    AppNavigation(
                        startDestination = if (authorized) MAIN_MENU_SCREEN_ID else AUTH_SCREEN_ID,
                        modifier = Modifier
                            .fillMaxSize()
                            .background(AppTheme.colors.backgroundPrimary)
                    )
                }
            }
        }
        if (!Environment.isExternalStorageManager()) {
            requestAllFilesPermissions()
        }
    }

    private fun requestAllFilesPermissions() = try {
        val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            .apply {
                addCategory("android.intent.category.DEFAULT")
            }.also {
                it.data = Uri.parse("package:${this.packageName}")
            }
        requestAllFilesAccess.launch(intent)
    } catch (e: Exception) {
        val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
        requestAllFilesAccess.launch(intent)
    }
}

package ru.geowork.photoapp.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.core.app.ActivityCompat
import ru.geowork.photoapp.R
import ru.geowork.photoapp.util.hasPermission

@Composable
fun WithPermissions(
    requestedPermissions: Array<String>,
    rationaleText: String = stringResource(R.string.permissions_not_granted_title),
    blockedRationaleText: String = stringResource(R.string.permissions_not_granted_blocked_text),
    dismissButtonText: String = stringResource(R.string.permissions_not_granted_exit),
    confirmButtonText: String = stringResource(R.string.permissions_not_granted_retry),
    onDismiss: () -> Unit,
    content: @Composable () -> Unit = {}
) {
    val context = LocalContext.current
    val activity = LocalActivity.current

    var isBlocked by rememberSaveable { mutableStateOf(false) }
    var canShowContent by rememberSaveable { mutableStateOf(false) }
    var showRationaleDialog by rememberSaveable { mutableStateOf(false) }

    val requestPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (isGranted) {
            canShowContent = true
        } else {
            if (activity != null) {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, requestedPermissions.first())) {
                    isBlocked = true
                }
                showRationaleDialog = true
            } else {
                onDismiss()
            }
        }
    }

    val requestMultiplePermissionsLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { grantedMap -> }

    LaunchedEffect(Unit) {
        when(requestedPermissions.size) {
            0 -> { canShowContent = true }
            1 -> {
                val permission = requestedPermissions.first()
                if (context.hasPermission(permission)) {
                    canShowContent = true
                } else {
                    requestPermissionLauncher.launch(requestedPermissions.first())
                }
            }
            else -> {
                val deniedPermissions = requestedPermissions.filter { !context.hasPermission(it) }
                if (deniedPermissions.isEmpty()) {
                    canShowContent = true
                } else {
                    requestMultiplePermissionsLauncher.launch(deniedPermissions.toTypedArray())
                }
            }
        }
    }

    if (showRationaleDialog) {
        RationaleDialog(
            isBlocked = isBlocked,
            rationaleText = if (isBlocked) blockedRationaleText else rationaleText,
            dismissButtonText = dismissButtonText,
            confirmButtonText = confirmButtonText,
            onDismiss = onDismiss,
            onConfirm = {
                showRationaleDialog = false
                if (isBlocked) {
                    context.openAppSettings()
                    onDismiss()
                } else {
                    when(requestedPermissions.size) {
                        0 -> { canShowContent = true }
                        1 -> requestPermissionLauncher.launch(requestedPermissions.first())
                        else -> requestMultiplePermissionsLauncher.launch(requestedPermissions)
                    }
                }
            }
        )
    }

    if (canShowContent) {
        content()
    }
}

private fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        .apply {
            addCategory("android.intent.category.DEFAULT")
        }.also {
            it.data = Uri.parse("package:$packageName")
        }
    startActivity(intent)
}

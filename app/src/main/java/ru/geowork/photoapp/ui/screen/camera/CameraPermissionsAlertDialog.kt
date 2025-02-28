package ru.geowork.photoapp.ui.screen.camera

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.components.AppDialog

@Composable
fun CameraPermissionsAlertDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AppDialog(
        dismissButtonText = stringResource(R.string.permissions_not_granted_exit),
        confirmButtonText = stringResource(R.string.permissions_not_granted_retry),
        title = stringResource(R.string.permissions_not_granted_title),
        onDismiss = onDismiss,
        onConfirm = onConfirm
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            text = stringResource(R.string.camera_permissions_not_granted_text)
        )
    }
}

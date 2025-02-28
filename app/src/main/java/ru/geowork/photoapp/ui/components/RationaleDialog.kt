package ru.geowork.photoapp.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.R

@Composable
fun RationaleDialog(
    isBlocked: Boolean = false,
    rationaleText: String = stringResource(R.string.permissions_not_granted_title),
    dismissButtonText: String = stringResource(R.string.permissions_not_granted_exit),
    confirmButtonText: String = stringResource(R.string.permissions_not_granted_retry),
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    AppDialog(
        dismissButtonText = dismissButtonText,
        confirmButtonText =confirmButtonText,
        title = if (isBlocked) {
            stringResource(R.string.permissions_not_granted_blocked_title)
        } else {
            stringResource(R.string.permissions_not_granted_title)
        },
        onDismiss = onDismiss,
        onConfirm = onConfirm
    ) {
        Text(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
            text = rationaleText
        )
    }
}

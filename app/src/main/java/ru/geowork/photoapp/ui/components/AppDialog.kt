package ru.geowork.photoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.theme.AppTheme
import ru.geowork.photoapp.util.noRippleClickable

@Composable
fun AppDialog(
    title: String? = null,
    dismissButtonText: String = stringResource(R.string.cancel),
    confirmButtonText: String = stringResource(R.string.save),
    dismissButtonColors: ButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = AppTheme.colors.contentBackground,
        contentColor = AppTheme.colors.contentPrimary
    ),
    confirmButtonColors: ButtonColors = ButtonDefaults.buttonColors(
        backgroundColor = AppTheme.colors.accentBackground,
        contentColor = AppTheme.colors.accentPrimary
    ),
    onDismiss: (() -> Unit)? = null,
    onConfirm: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit
) {
    Dialog(onDismissRequest = onDismiss ?: {}) {

        val focusManager = LocalFocusManager.current

        Column(
            modifier = Modifier
                .background(AppTheme.colors.backgroundModal, RoundedCornerShape(20.dp))
                .noRippleClickable { focusManager.clearFocus() }
        ) {
            title?.let {
                Text(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 24.dp),
                    text = it,
                    style = AppTheme.typography.medium20,
                    color = AppTheme.colors.contentPrimary
                )
            }

            content()

            Row(
                modifier = Modifier.fillMaxWidth().padding(24.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                onDismiss?.let {
                    ButtonLarge(
                        modifier = Modifier.weight(1f),
                        colors = dismissButtonColors,
                        onClick = { it() }
                    ) {
                        Text(
                            text = dismissButtonText,
                            style = AppTheme.typography.semibold16
                        )
                    }
                }

                onConfirm?.let {
                    ButtonLarge(
                        modifier = Modifier.weight(1f),
                        colors = confirmButtonColors,
                        onClick = { it() }
                    ) {
                        Text(
                            text = confirmButtonText,
                            style = AppTheme.typography.semibold16
                        )
                    }
                }
            }
        }
    }
}

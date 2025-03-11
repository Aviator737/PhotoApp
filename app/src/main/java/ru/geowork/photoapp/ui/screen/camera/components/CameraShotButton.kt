package ru.geowork.photoapp.ui.screen.camera.components

import android.view.HapticFeedbackConstants
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.ui.theme.AppTheme
import ru.geowork.photoapp.ui.theme.ContentSubPrimaryDark

@Composable
fun CameraShotButton(
    onClick: () -> Unit
) {
    val haptic = LocalHapticFeedback.current

    Box(
        modifier = Modifier
            .background(AppTheme.colors.contentConstant, CircleShape)
            .size(70.dp)
            .clip(CircleShape)
            .clickable {
                haptic.performHapticFeedback(HapticFeedbackType(HapticFeedbackConstants.LONG_PRESS))
                onClick()
            },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(ContentSubPrimaryDark, CircleShape)
                .size(60.dp)
                .clip(CircleShape)
        )
    }
}

@Preview
@Composable
fun PreviewCameraShotButton() {
    AppTheme {
        CameraShotButton {}
    }
}

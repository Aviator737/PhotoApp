package ru.geowork.photoapp.ui.screen.camera.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.round
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun CameraFocusPoint(offset: Offset, showFocusPoint: Boolean) {
    AnimatedVisibility(
        visible = showFocusPoint,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .offset { offset.round() }
            .offset((-24).dp, (-24).dp)
    ) {
        Spacer(Modifier.border(2.dp, AppTheme.colors.contentConstant, CircleShape).size(48.dp))
    }
}

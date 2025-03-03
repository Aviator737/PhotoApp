package ru.geowork.photoapp.ui.screen.camera.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.geowork.photoapp.ui.theme.AppTheme
import ru.geowork.photoapp.ui.theme.BackgroundPrimaryDark
import ru.geowork.photoapp.ui.theme.ContentPrimaryLight
import ru.geowork.photoapp.util.noRippleClickable
import java.util.Locale

@Composable
fun ZoomButton(
    modifier: Modifier = Modifier,
    value: Float,
    isActive: Boolean,
    onClick: () -> Unit
) {
    val bgColor = if (isActive) AppTheme.colors.contentConstant else BackgroundPrimaryDark.copy(alpha = 0.5f)
    val textColor = if (isActive) ContentPrimaryLight else AppTheme.colors.contentConstant
    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(24.dp))
            .size(36.dp)
            .clip(RoundedCornerShape(24.dp))
            .noRippleClickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = String.format(Locale.US, "%.1f", value) + "X",
            fontSize = 10.sp,
            color = textColor,
            textAlign = TextAlign.Center
        )
    }
}

@Preview
@Composable
fun PreviewZoomButton() {
    AppTheme {
        ZoomButton(
            value = 0.54906833f,
            isActive = false,
            onClick = {}
        )
    }
}

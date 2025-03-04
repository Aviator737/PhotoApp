package ru.geowork.photoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun Chip(
    text: String,
    onClick: () -> Unit = {}
) {
    val shape = RoundedCornerShape(4.dp)
    Box(
        modifier = Modifier
            .background(color = AppTheme.colors.contentBackground, shape = shape)
            .clip(shape)
            .clickable { onClick() }
            .padding(horizontal = 4.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = AppTheme.typography.medium16,
            color = AppTheme.colors.contentSubPrimary
        )
    }
}

@Preview
@Composable
fun PreviewChip() {
    AppTheme {
        Box(Modifier.padding(16.dp)) {
            Chip(text = "Chip") {}
        }
    }
}

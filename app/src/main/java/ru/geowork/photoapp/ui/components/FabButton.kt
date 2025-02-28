package ru.geowork.photoapp.ui.components

import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp

@Composable
fun FabButton(
    modifier: Modifier = Modifier,
    icon: Painter,
    onClick: () -> Unit
) {
    ButtonLarge(
        modifier = modifier.width(52.dp),
        onClick = onClick
    ) {
        Icon(painter = icon, contentDescription = null)
    }
}

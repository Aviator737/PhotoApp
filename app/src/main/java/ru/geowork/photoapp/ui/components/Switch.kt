package ru.geowork.photoapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun Switch(
    isOn: Boolean,
    onClick: (Boolean) -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp, 24.dp)
            .clickable { onClick(!isOn) }
            .background(
                color = if (isOn) AppTheme.colors.accentPrimary else AppTheme.colors.contentSecondary,
                shape = CircleShape
            )
            .clip(CircleShape)
            .padding(4.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(start = if (isOn) 16.dp else 0.dp)
                .size(16.dp)
                .clickable { onClick(!isOn) }
                .background(
                    color = AppTheme.colors.contentConstant,
                    shape = CircleShape
                ).clip(CircleShape)
        )
    }
}

@Preview
@Composable
fun PreviewSwitch() {
    AppTheme {
        Switch(true) {}
    }
}

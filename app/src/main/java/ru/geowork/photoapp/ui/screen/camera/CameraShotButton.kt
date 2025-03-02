package ru.geowork.photoapp.ui.screen.camera

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.ui.theme.AppTheme
import ru.geowork.photoapp.ui.theme.ContentSubPrimaryDark

@Composable
fun CameraShotButton(
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .background(AppTheme.colors.contentConstant, RoundedCornerShape(35.dp))
            .size(70.dp)
            .clip(RoundedCornerShape(35.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .background(ContentSubPrimaryDark, RoundedCornerShape(30.dp))
                .size(60.dp)
                .clip(RoundedCornerShape(30.dp))
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

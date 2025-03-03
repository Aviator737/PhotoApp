package ru.geowork.photoapp.ui.screen.camera.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun CameraGridOverlay(modifier: Modifier = Modifier) {

    val lineColor = AppTheme.colors.contentConstant.copy(alpha = 0.7f)

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        val thirdWidth = width / 3
        val thirdHeight = height / 3

        val lineStroke = 1.dp.toPx()

        // Вертикальные линии
        drawLine(lineColor, Offset(thirdWidth, 0f), Offset(thirdWidth, height), lineStroke)
        drawLine(lineColor, Offset(2 * thirdWidth, 0f), Offset(2 * thirdWidth, height), lineStroke)

        // Горизонтальные линии
        drawLine(lineColor, Offset(0f, thirdHeight), Offset(width, thirdHeight), lineStroke)
        drawLine(lineColor, Offset(0f, 2 * thirdHeight), Offset(width, 2 * thirdHeight), lineStroke)
    }
}

package ru.geowork.photoapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun PanelHeader(
    showRightButton: Boolean = false,
    showLeftButton: Boolean = true,
    title: String = "",
    centerTitle: Boolean = true,
    leftIcon: Painter = painterResource(id = R.drawable.arrow_left),
    rightIcon: Painter = painterResource(id = R.drawable.more_vertical),
    onRightButtonClick: () -> Unit = {},
    onLeftButtonClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable {
                    if (showLeftButton) {
                        onLeftButtonClick()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (showLeftButton) {
                Icon(
                    painter = leftIcon,
                    contentDescription = null
                )
            }
        }
        Text(
            modifier = Modifier
                .padding(horizontal = 4.dp)
                .weight(1f),
            text = title,
            textAlign = if (centerTitle) TextAlign.Center else TextAlign.Start,
            style = AppTheme.typography.medium20
        )
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(20.dp))
                .clickable {
                    if (showRightButton) {
                        onRightButtonClick()
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            if (showRightButton) {
                Icon(
                    painter = rightIcon,
                    contentDescription = null,
                    tint = AppTheme.colors.accentPrimary
                )
            }
        }
    }
}

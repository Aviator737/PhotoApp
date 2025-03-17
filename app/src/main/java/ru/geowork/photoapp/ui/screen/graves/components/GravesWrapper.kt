package ru.geowork.photoapp.ui.screen.graves.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.theme.AppTheme
import ru.geowork.photoapp.ui.theme.ContentPrimaryLight

@Composable
fun GravesWrapper(
    modifier: Modifier = Modifier,
    isEditMode: Boolean,
    showBackButton: Boolean,
    showOptionsButton: Boolean,
    onModeSwitchClick: (Boolean) -> Unit,
    onBackClick: () -> Unit,
    onOptionsClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Column(modifier = modifier
        .then(
            if (isEditMode) Modifier.border(
                border = BorderStroke(3.dp, AppTheme.colors.orange),
            ) else Modifier
        )
        .fillMaxSize()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showBackButton) {
                ClickZoneButton(
                    iconPainter = painterResource(R.drawable.arrow_left),
                    onClick = onBackClick
                )
            }
            //Spacer(modifier = Modifier.weight(1f))
            //ModeSwitch(isEditMode = isEditMode, onModeSwitchClick = onModeSwitchClick)
            Spacer(modifier = Modifier.weight(1f))
            if (showOptionsButton) {
                ClickZoneButton(
                    iconPainter = painterResource(R.drawable.more_vertical),
                    onClick = onOptionsClick
                )
            } else {
                Spacer(Modifier.size(40.dp))
            }
        }
        content()
    }
}

@Composable
private fun ClickZoneButton(
    iconPainter: Painter,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = iconPainter,
            contentDescription = null,
            tint = AppTheme.colors.contentPrimary
        )
    }
}

@Composable
private fun ModeSwitch(
    isEditMode: Boolean,
    onModeSwitchClick: (Boolean) -> Unit
) {
    Row(modifier = Modifier
        .background(
            AppTheme.colors.contentDisabled,
            RoundedCornerShape(20.dp)
        )
        .height(40.dp)
        .padding(3.dp)
    ) {
        Box(modifier = Modifier
            .clip(RoundedCornerShape(20.dp, 4.dp, 4.dp, 20.dp))
            .then(
                if (!isEditMode) Modifier.background(
                    color = AppTheme.colors.backgroundPrimary
                ) else Modifier
            )
            .clickable { onModeSwitchClick(false) }
            .fillMaxHeight()
            .padding(horizontal = 8.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.graves_normal_mode),
                style = AppTheme.typography.semibold16,
                color = AppTheme.colors.contentPrimary
            )
        }
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp, 20.dp, 20.dp, 4.dp))
                .then(
                    if (isEditMode) Modifier.background(
                        color = AppTheme.colors.orange
                    ) else Modifier
                )
                .clickable { onModeSwitchClick(true) }
                .fillMaxHeight()
                .padding(horizontal = 8.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stringResource(R.string.graves_edit_mode),
                style = AppTheme.typography.semibold16,
                color = if (isEditMode) {
                    ContentPrimaryLight
                } else AppTheme.colors.contentPrimary
            )
        }
    }
}

@Preview
@Composable
fun PreviewModeSwitch() {
    AppTheme {
        ModeSwitch(isEditMode = true) {}
    }
}

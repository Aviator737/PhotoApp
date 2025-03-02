package ru.geowork.photoapp.ui.components

import androidx.compose.animation.core.animateIntAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun DropdownInput(
    hint: String = "",
    text: String = "",
    icon: Painter? = null,
    enabled: Boolean = true,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val hintFontSize by animateIntAsState(
        targetValue = if (text.isNotEmpty()) 12 else 16,
        label = "hint fontSize"
    )

    val hintLineHeight by animateIntAsState(
        targetValue = if (text.isNotEmpty()) 16 else 24,
        label = "hint lineHeight"
    )

    Row(modifier = modifier
        .border(
            border = BorderStroke(2.dp, AppTheme.colors.contentBorder),
            shape = RoundedCornerShape(12.dp),
        )
        .background(
            color = AppTheme.colors.backgroundPrimary,
            shape = RoundedCornerShape(12.dp)
        )
        .clip(RoundedCornerShape(12.dp))
        .clickable { if (enabled) onClick() }
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        icon?.let {
            Image(
                painter = it,
                contentDescription = null,
                modifier = Modifier.size(24.dp).padding(end = 8.dp)
            )
        }
        Column(
            modifier = Modifier.height(40.dp).weight(1f),
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = hint,
                color = AppTheme.colors.contentSecondary,
                style = AppTheme.typography.medium16,
                fontSize = hintFontSize.sp,
                lineHeight = hintLineHeight.sp
            )
            if (text.isNotEmpty()) {
                Text(
                    text = text,
                    color = AppTheme.colors.contentPrimary,
                    style = AppTheme.typography.regular16
                )
            }
        }
        Image(
            painter = painterResource(R.drawable.ic_chevron_down),
            contentDescription = null,
            modifier = Modifier.padding(start = 8.dp).size(24.dp),
            colorFilter = ColorFilter.tint(AppTheme.colors.contentSecondary)
        )
    }
}

@Preview
@Composable
fun PreviewDropdownInput() {
    AppTheme {
        DropdownInput(
            hint = stringResource(R.string.auth_supervisor_name),
            text = ""
        )
    }
}

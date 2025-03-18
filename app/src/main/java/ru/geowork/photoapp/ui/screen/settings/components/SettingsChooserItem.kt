package ru.geowork.photoapp.ui.screen.settings.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun SettingsChooserItem(
    modifier: Modifier = Modifier,
    text: String,
    descriptionText: String? = null,
    icon: Painter? = null,
    selectedText: String? = null,
    onClick: () -> Unit,
    chooser: (@Composable () -> Unit)? = null
) {
    chooser?.invoke()

    Row(
        modifier = Modifier.fillMaxWidth().clickable { onClick() }.then(modifier),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            icon?.let {
                Icon(
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .size(24.dp),
                    painter = it,
                    contentDescription = null,
                    tint = AppTheme.colors.contentPrimary
                )
            }
            Column {
                Text(
                    text = text,
                    color = AppTheme.colors.contentPrimary,
                    style = AppTheme.typography.medium16
                )
                descriptionText?.let {
                    Text(
                        modifier = Modifier.padding(top = 2.dp),
                        text = it,
                        color = AppTheme.colors.contentSecondary,
                        style = AppTheme.typography.regular14
                    )
                }
            }
        }
        Row(
            modifier = Modifier.width(150.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            selectedText?.let {
                Text(
                    modifier = Modifier.weight(1f),
                    text = it,
                    color = AppTheme.colors.contentPrimary,
                    style = AppTheme.typography.medium16,
                    textAlign = TextAlign.Center
                )
            }
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.chevron_right),
                contentDescription = null,
                tint = AppTheme.colors.contentPrimary
            )
        }
    }
}

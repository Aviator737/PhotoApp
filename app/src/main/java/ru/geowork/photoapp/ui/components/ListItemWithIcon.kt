package ru.geowork.photoapp.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun ListItemWithIcon(
    modifier: Modifier = Modifier,
    name: String,
    icon: Painter? = null,
    endIcon: Painter? = null,
    onClick: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(name) }
            .then(modifier)
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = 14.dp,
                bottom = 14.dp
            ),
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
            Text(
                text = name,
                color = AppTheme.colors.contentPrimary,
                style = AppTheme.typography.medium16
            )
        }
        endIcon?.let {
            Icon(
                modifier = Modifier.size(24.dp),
                painter = painterResource(id = R.drawable.chevron_right),
                contentDescription = null,
                tint = AppTheme.colors.contentPrimary
            )
        }
    }
}


@Composable
@Preview
private fun ListItemWithIconPreview() {
    AppTheme {
        ListItemWithIcon(
            name = "Тест папка"
        ) {}
    }
}


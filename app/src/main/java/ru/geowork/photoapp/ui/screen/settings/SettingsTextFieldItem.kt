package ru.geowork.photoapp.ui.screen.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.components.Input
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun SettingsTextFieldItem(
    text: String,
    descriptionText: String? = null,
    icon: Painter? = null,
    enabled: Boolean = true,
    hint: String,
    value: String,
    keyboardType: KeyboardType = KeyboardType.Unspecified,
    onInput: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(
                start = 12.dp,
                end = 12.dp,
                top = 14.dp,
                bottom = 14.dp
            )
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
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
        Input(
            modifier = Modifier.padding(start = 16.dp).width(80.dp),
            hint = hint,
            text = value,
            enabled = enabled,
            keyboardType = keyboardType,
            onInput = onInput
        )
    }
}

@Preview
@Composable
fun PreviewSettingsTextFieldItem() {
    AppTheme {
        SettingsTextFieldItem(
            text = "Тест настройка",
            hint = "10-100",
            value = "100",
            icon = painterResource(R.drawable.ic_settings),
            onInput = {},
        )
    }
}

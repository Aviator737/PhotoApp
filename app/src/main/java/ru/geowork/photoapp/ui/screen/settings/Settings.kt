package ru.geowork.photoapp.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.BuildConfig
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.theme.AppTheme
import ru.geowork.photoapp.util.noRippleClickable

@Composable
fun Settings(
    state: SettingsUiState,
    onUiAction: (SettingsUiAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier
        .fillMaxSize()
        .safeDrawingPadding()
        .noRippleClickable { focusManager.clearFocus() }
    ) {
        state.maxImageSize?.let { quality ->
            SettingsTextFieldItem(
                text = stringResource(R.string.settings_image_max_size_title),
                descriptionText = stringResource(R.string.settings_image_max_size_description),
                icon = painterResource(R.drawable.ic_image),
                hint = stringResource(R.string.settings_image_max_size_hint),
                keyboardType = KeyboardType.Number,
                enabled = false,
                value = quality,
                onInput = { onUiAction(SettingsUiAction.OnImageMaxSizeInput(it)) }
            )
        }
        Spacer(Modifier.weight(1f))
        Text(
            modifier = Modifier
                .padding(
                    start = 12.dp,
                    end = 12.dp,
                    bottom = 14.dp
                )
                .fillMaxWidth(),
            text = "Версия приложения - ${BuildConfig.VERSION_NAME}",
            color = AppTheme.colors.contentPrimary,
            style = AppTheme.typography.medium16
        )
    }
}

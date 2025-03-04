package ru.geowork.photoapp.ui.screen.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import ru.geowork.photoapp.R
import ru.geowork.photoapp.util.noRippleClickable

@Composable
fun Settings(
    state: SettingsUiState,
    onUiAction: (SettingsUiAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    Column(modifier = Modifier.fillMaxSize().noRippleClickable {
        focusManager.clearFocus()
    }) {
        state.imageQuality?.let { quality ->
            SettingsTextFieldItem(
                text = stringResource(R.string.settings_image_quality_title),
                icon = painterResource(R.drawable.ic_image),
                hint = stringResource(R.string.settings_image_quality_hint),
                keyboardType = KeyboardType.Number,
                value = quality,
                onInput = { onUiAction(SettingsUiAction.OnImageQualityInput(it)) }
            )
        }
    }
}

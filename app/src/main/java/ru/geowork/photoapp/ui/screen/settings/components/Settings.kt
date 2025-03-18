package ru.geowork.photoapp.ui.screen.settings.components

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalZeroShutterLag
import androidx.camera.core.ImageCapture
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.BuildConfig
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.components.AppDialog
import ru.geowork.photoapp.ui.screen.settings.SettingsUiAction
import ru.geowork.photoapp.ui.screen.settings.SettingsUiState
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
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
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
        state.captureModeState.captureMode?.let { mode ->
            SettingsChooserItem(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
                text = stringResource(R.string.settings_capture_mode_title),
                icon = painterResource(R.drawable.ic_photo),
                selectedText = getCaptureModeTitleByInt(mode),
                onClick = { onUiAction(SettingsUiAction.OnCaptureModeOpenChooser) },
                chooser = state.captureModeState.chooser?.let {
                    {
                        AppDialog(
                            title = stringResource(R.string.settings_select_capture_mode),
                            onDismiss = { onUiAction(SettingsUiAction.OnCaptureModeSelectorDismiss) },
                            onConfirm = { onUiAction(SettingsUiAction.OnCaptureModeSelectorConfirm) }
                        ) {
                            Column(modifier = Modifier.selectableGroup().padding(top = 12.dp)) {
                                it.options.forEach { option ->
                                    Row(modifier = Modifier
                                        .fillMaxWidth()
                                        .selectable(
                                            selected = option == it.selected,
                                            onClick = { onUiAction(SettingsUiAction.OnCaptureModeSelected(option)) },
                                            role = Role.RadioButton
                                        )
                                        .padding(horizontal = 24.dp, vertical = 12.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(selected = option == it.selected, onClick = null)
                                        Column(
                                            modifier = Modifier.padding(horizontal = 16.dp),
                                            verticalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            Text(
                                                text = getCaptureModeTitleByInt(option),
                                                color = AppTheme.colors.contentPrimary,
                                                style = AppTheme.typography.medium16,
                                            )
                                            Text(
                                                text = getCaptureModeHintByInt(option),
                                                color = AppTheme.colors.contentSecondary,
                                                style = AppTheme.typography.regular14
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
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
            color = AppTheme.colors.contentSecondary,
            style = AppTheme.typography.medium16,
            textAlign = TextAlign.Center
        )
    }
}

@OptIn(ExperimentalZeroShutterLag::class)
@Composable
private fun getCaptureModeTitleByInt(value: Int): String = when(value) {
    ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY -> stringResource(R.string.settings_select_capture_quality)
    ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY -> stringResource(R.string.settings_select_capture_min_latency)
    ImageCapture.CAPTURE_MODE_ZERO_SHUTTER_LAG -> stringResource(R.string.settings_select_capture_zero_lag)
    else -> throw IllegalStateException("Unknown capture mode")
}

@OptIn(ExperimentalZeroShutterLag::class)
@Composable
private fun getCaptureModeHintByInt(value: Int): String = when(value) {
    ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY -> stringResource(R.string.settings_select_capture_quality_hint)
    ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY -> stringResource(R.string.settings_select_capture_min_latency_hint)
    ImageCapture.CAPTURE_MODE_ZERO_SHUTTER_LAG -> stringResource(R.string.settings_select_capture_zero_lag_hint)
    else -> throw IllegalStateException("Unknown capture mode")
}

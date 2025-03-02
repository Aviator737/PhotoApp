package ru.geowork.photoapp.ui.screen.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.RadioButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.components.AppDialog
import ru.geowork.photoapp.ui.components.ButtonLarge
import ru.geowork.photoapp.ui.components.DropdownInput
import ru.geowork.photoapp.ui.components.EditSelectableEditableInput
import ru.geowork.photoapp.ui.components.Input
import ru.geowork.photoapp.ui.theme.AppTheme
import ru.geowork.photoapp.util.noRippleClickable

@Composable
fun Auth(
    state: AuthUiState,
    onUiAction: (AuthUiAction) -> Unit
) {
    val focusManager = LocalFocusManager.current

    val endGreeting = if (
        state.photographName.isNotEmpty() &&
        state.photographName.isNotBlank()
    ) {
        ",\n${state.photographName}"
    } else ""

    state.selectSupervisorDialog?.let { data ->
        AppDialog(
            title = stringResource(R.string.auth_supervisor_name),
            onDismiss = { onUiAction(AuthUiAction.OnDismissSupervisorSelect) },
            onConfirm = { onUiAction(AuthUiAction.OnConfirmSupervisorSelect) }
        ) {
            LazyColumn(
                modifier = Modifier
                    .selectableGroup()
                    .weight(weight = 1f, fill = false)
                    .fillMaxWidth()
                    .padding(top = 12.dp)
            ) {
                items(data) { item ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onUiAction(AuthUiAction.OnSupervisorSelect(item.first))
                                focusManager.clearFocus()
                            }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = item.second,
                            onClick = {
                                onUiAction(AuthUiAction.OnSupervisorSelect(item.first))
                                focusManager.clearFocus()
                            }
                        )
                        Text(
                            text = item.first,
                            style = AppTheme.typography.medium16,
                            color = AppTheme.colors.contentPrimary
                        )
                    }
                }
                item {
                    EditSelectableEditableInput(
                        isSelected = state.isCustomSupervisorNameSelected,
                        hint = stringResource(R.string.auth_supervisor_custom),
                        text = state.customSupervisorName,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 8.dp),
                        onClick = { onUiAction(AuthUiAction.OnCustomSupervisorInputClick) },
                        onInput = { onUiAction(AuthUiAction.OnCustomSupervisorInput(it)) }
                    )
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxSize()
            .noRippleClickable { focusManager.clearFocus() },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = Modifier.padding(top = 57.dp),
            painter = painterResource(R.drawable.logo),
            contentDescription = null,
            colorFilter = ColorFilter.tint(AppTheme.colors.contentPrimary)
        )

        Spacer(modifier = Modifier.weight(0.2f))

        Text(
            text = stringResource(R.string.auth_greeting) + endGreeting + "!",
            style = AppTheme.typography.semibold32,
            textAlign = TextAlign.Center,
            color = AppTheme.colors.contentPrimary
        )
        if (endGreeting.isEmpty()) {
            Text(text = " ", style = AppTheme.typography.semibold32)
        }

        Spacer(modifier = Modifier.weight(0.2f))

        Input(
            hint = stringResource(R.string.auth_photograph_name),
            text = state.photographName,
            onInput = { onUiAction(AuthUiAction.OnPhotographNameInput(it)) }
        )

        DropdownInput(
            hint = stringResource(R.string.auth_supervisor_name),
            text = state.supervisorName,
            modifier = Modifier.padding(top = 16.dp),
            onClick = {
                focusManager.clearFocus()
                onUiAction(AuthUiAction.OnSelectSupervisorClick)
            }
        )

        Spacer(modifier = Modifier.weight(0.6f))

        ButtonLarge(
            modifier = Modifier.fillMaxWidth().padding(bottom = 54.dp),
            enabled = state.canGoNext,
            onClick = { onUiAction(AuthUiAction.OnNextClick) }
        ) {
            Text(
                text = stringResource(id = R.string.next),
                style = AppTheme.typography.semibold16
            )
        }
    }
}

@Composable
@Preview
fun AuthPreview() {
    AppTheme {
        Auth(state = AuthUiState()) {}
    }
}

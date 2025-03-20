package ru.geowork.photoapp.ui.screen.graves.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Checkbox
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.R
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.components.AppDialog
import ru.geowork.photoapp.ui.components.Input
import ru.geowork.photoapp.ui.screen.graves.GraveyardsUiState
import ru.geowork.photoapp.ui.screen.graves.getFolderTypeNameByLevel
import ru.geowork.photoapp.ui.theme.AppTheme
import ru.geowork.photoapp.util.noRippleClickable

@Composable
fun CreateFolderItemDialog(
    state: GraveyardsUiState.NewFolderDialogState,
    onEditModeCheckBoxClick: (Boolean) -> Unit,
    onNameInput: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    AppDialog(
        title = "${stringResource(R.string.add)} ${getFolderTypeNameByLevel(state.item.level)}",
        onDismiss = onDismiss,
        onConfirm = onConfirm
    ) {
        Input(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp).focusRequester(focusRequester),
            hint = "${stringResource(R.string.add)} ${getFolderTypeNameByLevel(state.item.level)}",
            text = state.item.name,
            initialFocusIndex = state.focusIndex,
            onInput = onNameInput
        )

        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .noRippleClickable { onEditModeCheckBoxClick(!state.isEditMode) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = state.isEditMode,
                onCheckedChange = onEditModeCheckBoxClick
            )
            Text(
                modifier = Modifier.padding(start = 4.dp),
                text = stringResource(R.string.edit_mode_postfix),
                style = AppTheme.typography.regular16,
                color = AppTheme.colors.contentPrimary
            )
        }
    }
}

@Preview
@Composable
fun PreviewCreateFolderItemDialog() {
    AppTheme {
        CreateFolderItemDialog(
            state = GraveyardsUiState.NewFolderDialogState(
                item = FolderItem.Folder(name = "test", level = 0),
                focusIndex = 0,
                isEditMode = false,
                showEditModeCheckbox = true
            ), {}, {}, {}, {}
        )
    }
}

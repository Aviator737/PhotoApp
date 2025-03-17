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
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.R
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.components.AppDialog
import ru.geowork.photoapp.ui.components.Input
import ru.geowork.photoapp.ui.screen.graves.FolderLevel
import ru.geowork.photoapp.ui.screen.graves.GraveyardsUiState
import ru.geowork.photoapp.ui.theme.AppTheme
import ru.geowork.photoapp.util.noRippleClickable

@Composable
fun CreateFolderItemDialog(
    state: GraveyardsUiState.NewFolderItemDialogState,
    folderLevel: FolderLevel,
    onEditModeCheckBoxClick: (Boolean) -> Unit,
    onNameInput: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    AppDialog(
        title = when(state.item) {
            is FolderItem.Folder -> when(folderLevel) {
                FolderLevel.GRAVEYARDS -> stringResource(R.string.graves_add_graveyard)
                FolderLevel.BLOCKS -> stringResource(R.string.graves_add_block)
                FolderLevel.ROWS -> stringResource(R.string.graves_add_row)
            }
            is FolderItem.ImageFile -> stringResource(R.string.graves_add_image_file)
            is FolderItem.DocumentFile -> stringResource(R.string.graves_add_text_file)
        },
        onDismiss = onDismiss,
        onConfirm = onConfirm
    ) {
        Input(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp).focusRequester(focusRequester),
            hint = when(state.item) {
                is FolderItem.Folder -> when(folderLevel) {
                    FolderLevel.GRAVEYARDS -> stringResource(R.string.graves_add_graveyard)
                    FolderLevel.BLOCKS -> stringResource(R.string.graves_add_block)
                    FolderLevel.ROWS -> stringResource(R.string.graves_add_row)
                }
                is FolderItem.ImageFile -> ""
                is FolderItem.DocumentFile -> stringResource(R.string.graves_add_text_file_hint)
            },
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
                text = stringResource(R.string.graves_edit_mode_postfix),
                style = AppTheme.typography.regular16
            )
        }
    }
}

//@Preview
//@Composable
//fun PreviewCreateFolderItemDialog() {
//    AppTheme {
//        CreateFolderItemDialog(FolderItem.Folder(), 0, FolderLevel.BLOCKS, {}, {}, {})
//    }
//}

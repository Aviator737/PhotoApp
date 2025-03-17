package ru.geowork.photoapp.ui.screen.graves.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
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
import ru.geowork.photoapp.ui.screen.graves.FolderLevel
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun CreateFolderItemDialog(
    item: FolderItem,
    initialFocusIndex: Int = item.name.length,
    folderLevel: FolderLevel,
    onNameInput: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    AppDialog(
        title = when(item) {
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
            hint = when(item) {
                is FolderItem.Folder -> when(folderLevel) {
                    FolderLevel.GRAVEYARDS -> stringResource(R.string.graves_add_graveyard)
                    FolderLevel.BLOCKS -> stringResource(R.string.graves_add_block)
                    FolderLevel.ROWS -> stringResource(R.string.graves_add_row)
                }
                is FolderItem.ImageFile -> ""
                is FolderItem.DocumentFile -> stringResource(R.string.graves_add_text_file_hint)
            },
            text = item.name,
            initialFocusIndex = initialFocusIndex,
            onInput = onNameInput
        )
    }
}

@Preview
@Composable
fun PreviewCreateFolderItemDialog() {
    AppTheme {
        CreateFolderItemDialog(FolderItem.Folder(), 0, FolderLevel.BLOCKS, {}, {}, {})
    }
}

package ru.geowork.photoapp.ui.screen.graves.components

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.R
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.components.AppDialog
import ru.geowork.photoapp.ui.components.Input
import ru.geowork.photoapp.ui.screen.graves.GraveyardsUiState
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun CreatePhotoRowDocumentDialog(
    state: GraveyardsUiState.PhotoRowDocumentDialog,
    onTextInput: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    AppDialog(
        title = stringResource(R.string.site_count_title),
        onDismiss = onDismiss,
        onConfirm = onConfirm
    ) {
        Input(
            modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp).focusRequester(focusRequester),
            hint = stringResource(R.string.site_count_title),
            keyboardType = KeyboardType.Number,
            text = state.item.text ?: "",
            onInput = onTextInput
        )
    }
}

@Preview
@Composable
fun PreviewPhotoRowDocumentDialog() {
    AppTheme {
        CreatePhotoRowDocumentDialog(
            state = GraveyardsUiState.PhotoRowDocumentDialog(
                item = FolderItem.DocumentFile(name = "test"), FolderItem.Folder()
            ), {}, {}, {}
        )
    }
}

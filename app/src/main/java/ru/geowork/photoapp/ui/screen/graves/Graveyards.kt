package ru.geowork.photoapp.ui.screen.graves

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.R
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.components.AppDialog
import ru.geowork.photoapp.ui.components.FabButton
import ru.geowork.photoapp.ui.components.FileManager
import ru.geowork.photoapp.ui.components.Input
import ru.geowork.photoapp.ui.components.ListItemWithIcon

@Composable
fun Graveyards(
    state: GraveyardsUiState,
    onUiAction: (GraveyardsUiAction) -> Unit
) {
    val bottomSheetState = if (state.showBottomSheet) ModalBottomSheetValue.HalfExpanded else ModalBottomSheetValue.Hidden
    val sheetState = rememberModalBottomSheetState(bottomSheetState)

    if (sheetState.currentValue != ModalBottomSheetValue.Hidden) {
        DisposableEffect(Unit) {
            onDispose { onUiAction(GraveyardsUiAction.SetShowBottomSheet(false)) }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        ModalBottomSheetLayout(
            sheetState = sheetState,
            sheetShape = RoundedCornerShape(8.dp),
            sheetContent = {
                Column(
                    modifier = Modifier.padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ListItemWithIcon(
                        modifier = Modifier.padding(horizontal = 12.dp),
                        name = when(state.folderLevel) {
                            FolderLevel.GRAVEYARDS -> stringResource(R.string.graves_add_graveyard)
                            FolderLevel.BLOCKS -> stringResource(R.string.graves_add_block)
                            FolderLevel.ROWS -> stringResource(R.string.graves_add_row)
                        },
                        icon = painterResource(id = R.drawable.folder_add),
                        onClick = { onUiAction(GraveyardsUiAction.OnAddFolderClick) }
                    )
                    if (state.folderLevel == FolderLevel.ROWS) {
                        ListItemWithIcon(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            name = stringResource(R.string.graves_add_image_file),
                            icon = painterResource(id = R.drawable.attachment),
                            onClick = { onUiAction(GraveyardsUiAction.OnAddImageFileClick) }
                        )
                        ListItemWithIcon(
                            modifier = Modifier.padding(horizontal = 12.dp),
                            name = stringResource(R.string.graves_add_text_file),
                            icon = painterResource(id = R.drawable.ic_text_file),
                            onClick = { onUiAction(GraveyardsUiAction.OnAddTextFileClick) }
                        )
                    }
                }
            }
        ) {
            GravesWrapper(
                isEditMode = state.isEditMode,
                showBackButton = state.showBackButton,
                showOptionsButton = state.showOptionsButton,
                onModeSwitchClick = { onUiAction(GraveyardsUiAction.SetIsEditMode(it)) },
                onBackClick = { onUiAction(GraveyardsUiAction.OnBack) },
                onOptionsClick = {}
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    FileManager(
                        parentFolders = state.parentFolders,
                        folderItems = state.folderItems,
                        onTakePhotoClick = { onUiAction(GraveyardsUiAction.OnTakePhotoClick(it)) },
                        onParentFolderClick = { onUiAction(GraveyardsUiAction.OnParentFolderClick(it)) },
                        onFolderItemClick = { onUiAction(GraveyardsUiAction.OnFolderItemClick(it)) }
                    )
                    FabButton(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                        icon = painterResource(id = R.drawable.add),
                        onClick = { onUiAction(GraveyardsUiAction.SetShowBottomSheet(true)) }
                    )
                }
            }
        }

        state.newItemDialog?.let { item ->
            AppDialog(
                title = when(item) {
                    is FolderItem.Folder -> when(state.folderLevel) {
                        FolderLevel.GRAVEYARDS -> stringResource(R.string.graves_add_graveyard)
                        FolderLevel.BLOCKS -> stringResource(R.string.graves_add_block)
                        FolderLevel.ROWS -> stringResource(R.string.graves_add_row)
                    }
                    is FolderItem.ImageFile -> stringResource(R.string.graves_add_image_file)
                    is FolderItem.TextFile -> stringResource(R.string.graves_add_text_file)
                    else -> ""
                },
                onDismiss = { onUiAction(GraveyardsUiAction.OnDismissItemDialog) },
                onConfirm = { onUiAction(GraveyardsUiAction.OnItemNameConfirm) }
            ) {
                Input(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, top = 16.dp),
                    hint = when(item) {
                        is FolderItem.Folder -> when(state.folderLevel) {
                            FolderLevel.GRAVEYARDS -> stringResource(R.string.graves_add_graveyard)
                            FolderLevel.BLOCKS -> stringResource(R.string.graves_add_block)
                            FolderLevel.ROWS -> stringResource(R.string.graves_add_row)
                        }
                        is FolderItem.TextFile -> stringResource(R.string.graves_add_text_file_hint)
                        else -> ""
                    },
                    text = item.name,
                    onInput = { onUiAction(GraveyardsUiAction.OnItemNameInput(it)) }
                )
            }
        }
    }
}

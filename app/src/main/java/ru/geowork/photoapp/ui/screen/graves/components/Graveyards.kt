package ru.geowork.photoapp.ui.screen.graves.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.components.ButtonLarge
import ru.geowork.photoapp.ui.components.Chip
import ru.geowork.photoapp.ui.components.FileManager
import ru.geowork.photoapp.ui.components.PhotoViewerDialog
import ru.geowork.photoapp.ui.screen.graves.FolderLevel
import ru.geowork.photoapp.ui.screen.graves.GraveyardsUiAction
import ru.geowork.photoapp.ui.screen.graves.GraveyardsUiState
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun Graveyards(
    state: GraveyardsUiState,
    onUiAction: (GraveyardsUiAction) -> Unit
) {

    val pickPictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
        if (imageUri != null) {
            onUiAction(GraveyardsUiAction.OnAddExternalPicture(imageUri))
        }
    }

    LifecycleResumeEffect(Unit) {
        onUiAction(GraveyardsUiAction.OnUpdateFolderItems)
        onPauseOrDispose {}
    }

    state.photoView?.let {
        PhotoViewerDialog(it) { onUiAction(GraveyardsUiAction.OnClosePhoto) }
    }

    GravesWrapper(
        modifier = Modifier.safeDrawingPadding(),
        isEditMode = state.isEditMode,
        showBackButton = state.showBackButton,
        showOptionsButton = state.showOptionsButton,
        onModeSwitchClick = { onUiAction(GraveyardsUiAction.SetIsEditMode(it)) },
        onBackClick = { onUiAction(GraveyardsUiAction.OnBack) },
        onOptionsClick = {}
    ) {
        if (state.folderLevel == FolderLevel.GRAVEYARDS) {
            Box(modifier = Modifier.padding(top = 8.dp, start = 24.dp)) {
                Chip(stringResource(R.string.graves_choose_graveyard_title))
            }
        }
        FileManager(
            modifier = Modifier.fillMaxSize(),
            parentFolders = state.parentFolders,
            folderItems = state.folderItems,
            onTakePhotoClick = { onUiAction(GraveyardsUiAction.OnTakePhotoClick(it)) },
            onParentFolderClick = { onUiAction(GraveyardsUiAction.OnParentFolderClick(it)) },
            onFolderItemClick = { onUiAction(GraveyardsUiAction.OnFolderItemClick(it)) }
        ) {
            Column(
                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                ButtonLarge(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    colors = ButtonDefaults.buttonColors(
                        backgroundColor = AppTheme.colors.contentBackground,
                        contentColor = AppTheme.colors.contentPrimary
                    ),
                    onClick = { onUiAction(GraveyardsUiAction.OnAddFolderClick) }
                ) {
                    Text(
                        text = when(state.folderLevel) {
                            FolderLevel.GRAVEYARDS -> stringResource(R.string.graves_add_graveyard)
                            FolderLevel.BLOCKS -> stringResource(R.string.graves_add_block)
                            FolderLevel.ROWS -> stringResource(R.string.graves_add_row)
                        },
                        style = AppTheme.typography.semibold16
                    )
                }
                if (state.folderLevel == FolderLevel.ROWS) {
                    ButtonLarge(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = AppTheme.colors.contentBackground,
                            contentColor = AppTheme.colors.contentPrimary
                        ),
                        onClick = { pickPictureLauncher.launch("*/*") }
                    ) {
                        Text(
                            text = stringResource(R.string.graves_add_image_file),
                            style = AppTheme.typography.semibold16
                        )
                    }
                    ButtonLarge(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = AppTheme.colors.contentBackground,
                            contentColor = AppTheme.colors.contentPrimary
                        ),
                        onClick = { onUiAction(GraveyardsUiAction.OnAddTextFileClick) }
                    ) {
                        Text(
                            text = stringResource(R.string.graves_add_text_file),
                            style = AppTheme.typography.semibold16
                        )
                    }
                }
            }
        }
    }

    state.newItemDialog?.let { item ->
        CreateFolderItemDialog(
            item = item,
            folderLevel = state.folderLevel,
            onNameInput = { onUiAction(GraveyardsUiAction.OnItemNameInput(it)) },
            onDismiss = { onUiAction(GraveyardsUiAction.OnDismissItemDialog) },
            onConfirm = { onUiAction(GraveyardsUiAction.OnItemNameConfirm) }
        )
    }
}

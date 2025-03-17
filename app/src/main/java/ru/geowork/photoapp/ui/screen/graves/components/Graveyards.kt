package ru.geowork.photoapp.ui.screen.graves.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import ru.geowork.photoapp.ui.components.AppDialog
import ru.geowork.photoapp.ui.components.ButtonLarge
import ru.geowork.photoapp.ui.components.Chip
import ru.geowork.photoapp.ui.components.FileManager
import ru.geowork.photoapp.ui.screen.graves.FolderLevel
import ru.geowork.photoapp.ui.screen.graves.GraveyardsUiAction
import ru.geowork.photoapp.ui.screen.graves.GraveyardsUiState
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun Graveyards(
    state: GraveyardsUiState,
    onUiAction: (GraveyardsUiAction) -> Unit
) {

    val newBlockPrefix = stringResource(R.string.graves_add_block_prefix)
    val newRowPrefix = stringResource(R.string.graves_add_row_prefix)
    val editModePostfix = stringResource(R.string.graves_edit_mode_postfix)
    val folderPostfix = if (state.isEditMode) editModePostfix else ""

    val mapFileName = stringResource(R.string.graves_block_map_file_name)
    val pickFileLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
        imageUri?.let { onUiAction(GraveyardsUiAction.OnAddExternalFile(it, mapFileName)) }
    }

    LifecycleResumeEffect(Unit) {
        onUiAction(GraveyardsUiAction.OnUpdateFolderItems)
        onPauseOrDispose {}
    }

    GravesWrapper(
        modifier = Modifier.safeDrawingPadding(),
        isEditMode = state.isEditMode,
        showBackButton = state.showBackButton,
        showOptionsButton = state.showOptionsButton,
        onModeSwitchClick = { onUiAction(GraveyardsUiAction.SetIsEditMode(it)) },
        onBackClick = { onUiAction(GraveyardsUiAction.OnBack) },
        onOptionsClick = { onUiAction(GraveyardsUiAction.OnOptionsClick) }
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
            onFolderItemClick = { onUiAction(GraveyardsUiAction.OnFolderItemClick(it)) },
            onChildItemClick = { parent, child -> onUiAction(GraveyardsUiAction.OnChildItemClick(parent, child)) }
        ) {
            Column(
                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.folderLevel != FolderLevel.GRAVEYARDS) {
                    ButtonLarge(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = AppTheme.colors.contentBackground,
                            contentColor = AppTheme.colors.contentPrimary
                        ),
                        onClick = {
                            val prefix = when(state.folderLevel) {
                                FolderLevel.GRAVEYARDS -> ""
                                FolderLevel.BLOCKS -> newBlockPrefix
                                FolderLevel.ROWS -> newRowPrefix
                            }
                            onUiAction(GraveyardsUiAction.OnAddFolderClick(prefix, folderPostfix))
                        }
                    ) {
                        Text(
                            text = when (state.folderLevel) {
                                FolderLevel.GRAVEYARDS -> stringResource(R.string.graves_add_graveyard)
                                FolderLevel.BLOCKS -> stringResource(R.string.graves_add_block)
                                FolderLevel.ROWS -> stringResource(R.string.graves_add_row)
                            },
                            style = AppTheme.typography.semibold16
                        )
                    }
                }
                if (state.folderLevel == FolderLevel.ROWS) {
                    ButtonLarge(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = AppTheme.colors.contentBackground,
                            contentColor = AppTheme.colors.contentPrimary
                        ),
                        onClick = { pickFileLauncher.launch("*/*") }
                    ) {
                        Text(
                            text = stringResource(R.string.graves_add_image_file),
                            style = AppTheme.typography.semibold16
                        )
                    }
//                    ButtonLarge(
//                        modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
//                        colors = ButtonDefaults.buttonColors(
//                            backgroundColor = AppTheme.colors.contentBackground,
//                            contentColor = AppTheme.colors.contentPrimary
//                        ),
//                        onClick = { onUiAction(GraveyardsUiAction.OnAddTextFileClick) }
//                    ) {
//                        Text(
//                            text = stringResource(R.string.graves_add_text_file),
//                            style = AppTheme.typography.semibold16
//                        )
//                    }
                }
            }
        }
    }

    if (state.optionsDialog) {
        AppDialog(
            title = stringResource(R.string.graves_options_title),
            onDismiss = { onUiAction(GraveyardsUiAction.OnOptionsDismiss) },
            onConfirm = null
        ) {
            Spacer(modifier = Modifier.height(24.dp))
//            ButtonLarge(
//                modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
//                colors = ButtonDefaults.buttonColors(
//                    backgroundColor = AppTheme.colors.accentBackground,
//                    contentColor = AppTheme.colors.accentPrimary
//                ),
//                onClick = { onUiAction(GraveyardsUiAction.OnNavigateToUploadClick) }
//            ) {
//                Text(
//                    text = stringResource(R.string.graves_options_end),
//                    style = AppTheme.typography.semibold16
//                )
//            }
//            Spacer(modifier = Modifier.height(12.dp))
            ButtonLarge(
                modifier = Modifier.padding(horizontal = 24.dp).fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.systemErrorPrimary,
                    contentColor = AppTheme.colors.contentConstant
                ),
                onClick = { onUiAction(GraveyardsUiAction.OnDeleteRequestClick) }
            ) {
                Text(
                    text = stringResource(R.string.graves_options_remove_folder, state.parentFolders.last().name),
                    style = AppTheme.typography.semibold16
                )
            }
        }
    }

    state.newItemDialog?.let { item ->
        CreateFolderItemDialog(
            state = item,
            folderLevel = state.folderLevel,
            onEditModeCheckBoxClick = { onUiAction(GraveyardsUiAction.OnEditModeCheckboxClick(it, editModePostfix)) },
            onNameInput = { onUiAction(GraveyardsUiAction.OnItemNameInput(it, editModePostfix)) },
            onDismiss = { onUiAction(GraveyardsUiAction.OnDismissItemDialog) },
            onConfirm = { onUiAction(GraveyardsUiAction.OnItemNameConfirm) }
        )
    }

    state.deleteConfirmationDialog?.let {
        AppDialog(
            dismissButtonText = stringResource(R.string.cancel),
            confirmButtonText = stringResource(R.string.delete),
            title = stringResource(R.string.confirm_delete_title),
            confirmButtonColors = ButtonDefaults.buttonColors(
                backgroundColor = AppTheme.colors.systemErrorPrimary,
                contentColor = AppTheme.colors.contentConstant
            ),
            onDismiss = { onUiAction(GraveyardsUiAction.OnDeleteDismissClick) },
            onConfirm = { onUiAction(GraveyardsUiAction.OnDeleteConfirmedClick) }
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                text = stringResource(R.string.confirm_delete_text, it.name),
                color = AppTheme.colors.contentPrimary
            )
        }
    }
}

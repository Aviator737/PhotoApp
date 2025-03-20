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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.components.AppDialog
import ru.geowork.photoapp.ui.components.ButtonLarge
import ru.geowork.photoapp.ui.components.Chip
import ru.geowork.photoapp.ui.components.FileManager
import ru.geowork.photoapp.ui.screen.graves.GraveyardsUiAction
import ru.geowork.photoapp.ui.screen.graves.GraveyardsUiState
import ru.geowork.photoapp.ui.screen.graves.getFolderTypeNameByLevel
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun Graveyards(
    state: GraveyardsUiState,
    onUiAction: (GraveyardsUiAction) -> Unit
) {
    val context = LocalContext.current

    val editModePostfix = stringResource(R.string.edit_mode_postfix)
    val folderPostfix = if (state.isEditMode) editModePostfix else ""

    val mapFileName = stringResource(R.string.block_map_file_name)
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
        if (state.parentFolders.isEmpty()) {
            Box(modifier = Modifier.padding(top = 8.dp, start = 24.dp)) {
                Chip(stringResource(R.string.folder_l0_hint))
            }
        }
        FileManager(
            modifier = Modifier.fillMaxSize(),
            parentFolders = state.parentFolders,
            folderItems = state.folderItems,
            onTakePhotoClick = { onUiAction(GraveyardsUiAction.OnTakePhotoClick(it)) },
            onParentFolderClick = { onUiAction(GraveyardsUiAction.OnParentFolderClick(it)) },
            onFolderItemClick = { onUiAction(GraveyardsUiAction.OnFolderItemClick(it)) },
            onPhotoRowPhotoClick = { parent, child -> onUiAction(GraveyardsUiAction.OnPhotoRowPhotoClick(parent, child)) },
            onPhotoRowDocumentClick = { parent, child -> onUiAction(GraveyardsUiAction.OnPhotoRowDocumentClick(parent, child)) },
        ) {
            Column(
                modifier = Modifier.padding(top = 12.dp, bottom = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (state.parentFolders.isNotEmpty()) {
                    ButtonLarge(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 24.dp),
                        colors = ButtonDefaults.buttonColors(
                            backgroundColor = AppTheme.colors.contentBackground,
                            contentColor = AppTheme.colors.contentPrimary
                        ),
                        onClick = {
                            val prefix = "${context.getFolderTypeNameByLevel(state.parentFolders.size, true)}_"
                            onUiAction(GraveyardsUiAction.OnAddFolderClick(prefix, folderPostfix))
                        }
                    ) {
                        Text(
                            text = "${stringResource(R.string.add)} ${getFolderTypeNameByLevel(state.parentFolders.size)}",
                            style = AppTheme.typography.semibold16
                        )
                    }
                }
                if (state.parentFolders.size > 1) {
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
                            text = stringResource(R.string.add_image_file),
                            style = AppTheme.typography.semibold16
                        )
                    }
                }
            }
        }
    }

    if (state.optionsDialog) {
        AppDialog(
            title = stringResource(R.string.options_title),
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
                    text = stringResource(R.string.options_remove_folder, state.parentFolders.last().name),
                    style = AppTheme.typography.semibold16
                )
            }
        }
    }

    state.newFolderDialog?.let { item ->
        CreateFolderItemDialog(
            state = item,
            onEditModeCheckBoxClick = { onUiAction(GraveyardsUiAction.OnEditModeCheckboxClick(it, editModePostfix)) },
            onNameInput = { onUiAction(GraveyardsUiAction.OnItemNameInput(it, editModePostfix)) },
            onDismiss = { onUiAction(GraveyardsUiAction.OnDismissItemDialog) },
            onConfirm = { onUiAction(GraveyardsUiAction.OnItemNameConfirm) }
        )
    }

    state.photoRowDocumentDialog?.let { item ->
        CreatePhotoRowDocumentDialog(
            state = item,
            onTextInput = { onUiAction(GraveyardsUiAction.OnPhotoRowDocumentDialogTextInput(it)) },
            onDismiss = { onUiAction(GraveyardsUiAction.OnPhotoRowDocumentDialogDismiss) },
            onConfirm = { onUiAction(GraveyardsUiAction.OnPhotoRowDocumentDialogConfirm) }
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

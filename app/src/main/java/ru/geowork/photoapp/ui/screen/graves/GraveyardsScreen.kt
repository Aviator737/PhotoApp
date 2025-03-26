package ru.geowork.photoapp.ui.screen.graves

import android.content.Context
import android.content.Intent
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptionsBuilder
import androidx.navigation.compose.composable
import ru.geowork.photoapp.SyncForegroundService.Companion.startForegroundArchiveAndUpload
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.screen.camera.CameraPayload
import ru.geowork.photoapp.ui.screen.gallery.GalleryPayload
import ru.geowork.photoapp.ui.screen.graves.components.Graveyards

const val GRAVEYARDS_SCREEN_ID = "graveyards_screen"

fun NavGraphBuilder.graveyardsScreen(
    onBack: () -> Unit,
    navigateToCamera: (CameraPayload) -> Unit,
    navigateToGallery: (GalleryPayload) -> Unit
) {
    composable(GRAVEYARDS_SCREEN_ID) {
        val viewModel: GraveyardsViewModel = hiltViewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()
        val uiEvents by viewModel.uiEvents.collectAsStateWithLifecycle()

        val context = LocalContext.current

        BackHandler(enabled = true, onBack = { viewModel.onUiAction(GraveyardsUiAction.OnBack) })

        uiEvents.firstOrNull()?.let { uiEvent ->
            LaunchedEffect(uiEvent) {
                when(uiEvent) {
                    is GraveyardsUiEvent.NavigateToCamera -> navigateToCamera(uiEvent.payload)
                    is GraveyardsUiEvent.NavigateToGallery -> navigateToGallery(uiEvent.payload)
                    is GraveyardsUiEvent.OpenInExternalApp -> context.openInExternalApp(uiEvent.item)
                    is GraveyardsUiEvent.StartForegroundArchiveAndUpload -> startForegroundArchiveAndUpload(context, uiEvent.path)
                    GraveyardsUiEvent.NavigateBack -> onBack()
                }
            }
            viewModel.onUiEventHandled(uiEvent)
        }

        Graveyards(
            state = uiState,
            onUiAction = { viewModel.onUiAction(it) }
        )
    }
}

fun NavController.navigateToGraveyardsScreen(builder: NavOptionsBuilder.() -> Unit = {}) {
    navigate(GRAVEYARDS_SCREEN_ID, builder)
}

private fun Context.openInExternalApp(item: FolderItem.DocumentFile) {
    val mimeType = when(item.type) {
        FolderItem.DocumentFile.DocumentType.PDF -> "application/pdf"
        FolderItem.DocumentFile.DocumentType.TXT,
        FolderItem.DocumentFile.DocumentType.UNKNOWN -> "text/plain"
        FolderItem.DocumentFile.DocumentType.JSON -> "application/json"
    }
    val intent = Intent()
    intent.setAction(Intent.ACTION_VIEW)
    intent.setDataAndType(item.uri, mimeType)
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    startActivity(intent)
}

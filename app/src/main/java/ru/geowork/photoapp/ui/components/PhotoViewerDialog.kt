package ru.geowork.photoapp.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.window.DialogProperties
import coil3.compose.AsyncImage
import ru.geowork.photoapp.R
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.components.zoomable.rememberZoomState
import ru.geowork.photoapp.ui.components.zoomable.zoomable

@Composable
fun PhotoViewerDialog(
    item: FolderItem.ImageFile,
    onDismiss: () -> Unit
) {
    val zoomState = rememberZoomState()

    AppDialog(
        title = item.name,
        dismissButtonText = stringResource(R.string.close),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismiss = onDismiss
    ) {
        AsyncImage(
            model = item.fullPath,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .weight(1f)
                .zoomable(zoomState = zoomState)
        )
    }
}

package ru.geowork.photoapp.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ru.geowork.photoapp.R
import ru.geowork.photoapp.model.FolderItem

@Composable
fun PhotoViewerDialog(
    item: FolderItem.ImageFile,
    onDismiss: () -> Unit
) {
    AppDialog(
        title = item.name,
        dismissButtonText = stringResource(R.string.close),
        onDismiss = onDismiss
    ) {
        AsyncImage(
            model = item.fullPath,
            contentDescription = null,
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
        )
    }
}

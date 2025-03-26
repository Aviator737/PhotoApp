package ru.geowork.photoapp.ui.screen.camera.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import ru.geowork.photoapp.model.FolderItem

@Composable
fun CameraPhotoRow(
    items: List<FolderItem.ImageFile>,
    onClick: (Int) -> Unit
) {
    if (items.isEmpty()) return

    val itemsScrollState = rememberLazyListState(
        initialFirstVisibleItemIndex = items.lastIndex
    )

    LaunchedEffect(items.size) {
        itemsScrollState.animateScrollToItem(items.lastIndex)
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        state = itemsScrollState,
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(items) { index, item ->
            AsyncImage(
                model = item.uri,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onClick(index) }
            )
        }
    }
}

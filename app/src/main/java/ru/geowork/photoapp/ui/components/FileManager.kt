package ru.geowork.photoapp.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.R
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun FileManager(
    modifier: Modifier = Modifier,
    parentFolders: List<String>,
    folderItems: List<FolderItem>,
    onTakePhotoClick: (FolderItem.PhotoRow) -> Unit,
    onParentFolderClick: (String) -> Unit,
    onFolderItemClick: (FolderItem) -> Unit,
) {
    val parentFoldersScrollState = rememberLazyListState()

    LaunchedEffect(parentFolders.size) {
        if (parentFolders.isNotEmpty()) {
            parentFoldersScrollState.animateScrollToItem(parentFolders.lastIndex)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            state = parentFoldersScrollState,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
        ) {
            itemsIndexed(parentFolders) { i, parentFolder ->
                Chip(parentFolder) { onParentFolderClick(parentFolder) }
                if (i+1 < parentFolders.size) {
                    Icon(
                        modifier = Modifier.padding(horizontal = 4.dp).size(24.dp),
                        painter = painterResource(R.drawable.chevron_right),
                        contentDescription = null,
                        tint = AppTheme.colors.contentPrimary
                    )
                }
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp),
        ) {
            items(
                items = folderItems,
                key = { "${it.name}-${it.path}" }
            ) { item ->
                when(item) {
                    is FolderItem.PhotoRow -> PhotoRow(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        name = item.name,
                        onTakePhotoClick = { onTakePhotoClick(item) }
                    )
                    else -> ListItemWithIcon(
                        name = item.name,
                        icon = when (item) {
                            is FolderItem.Folder -> painterResource(R.drawable.folder)
                            is FolderItem.ImageFile -> painterResource(R.drawable.ic_attachment)
                            is FolderItem.TextFile -> painterResource(R.drawable.ic_text_file)
                            else -> painterResource(R.drawable.ic_folder_disabled)
                        },
                        endIcon = when(item) {
                            is FolderItem.Folder -> painterResource(R.drawable.chevron_right)
                            else -> null
                        },
                        onClick = { onFolderItemClick(item) }
                    )
                }
            }
        }
    }
}

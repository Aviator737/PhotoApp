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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.R
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun FileManager(
    modifier: Modifier = Modifier,
    parentFolders: List<FolderItem.Folder>,
    folderItems: List<FolderItem>,
    notes: Map<String, String>,
    isReadOnly: Boolean,
    onTakePhotoClick: (FolderItem.Folder) -> Unit,
    onParentFolderClick: (FolderItem.Folder) -> Unit,
    onFolderItemClick: (FolderItem) -> Unit,
    onPhotoRowPhotoClick: (parent: FolderItem.Folder, image: FolderItem.ImageFile) -> Unit,
    onPhotoRowNoteClick: (parent: FolderItem.Folder) -> Unit,
    notification: @Composable () -> Unit,
    createButtons: @Composable () -> Unit
) {
    val parentFoldersScrollState = rememberLazyListState()

    LaunchedEffect(parentFolders.size) {
        if (parentFolders.isNotEmpty()) {
            //parentFoldersScrollState.animateScrollToItem(parentFolders.lastIndex)
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        LazyRow(
            state = parentFoldersScrollState,
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            itemsIndexed(parentFolders) { i, parentFolder ->
                Chip(parentFolder.name) { onParentFolderClick(parentFolder) }
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
        notification()
        LazyColumn {
            items(
                items = folderItems,
                key = { it.id }
            ) { item ->
                when {
                    item is FolderItem.Folder && item.childItems != null -> PhotoRow(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp),
                        folder = item,
                        note = notes[item.name].orEmpty(),
                        isReadOnly = isReadOnly,
                        onTakePhotoClick = { onTakePhotoClick(item) },
                        onPhotoClick = { onPhotoRowPhotoClick(item, it) },
                        onNoteClick = { onPhotoRowNoteClick(item) }
                    )
                    else -> ListItemWithIcon(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        name = item.name,
                        icon = when (item) {
                            is FolderItem.Folder -> painterResource(R.drawable.folder)
                            is FolderItem.ImageFile -> painterResource(R.drawable.ic_attachment)
                            is FolderItem.DocumentFile -> when(item.type) {
                                FolderItem.DocumentFile.DocumentType.PDF -> painterResource(R.drawable.ic_pdf)
                                FolderItem.DocumentFile.DocumentType.TXT,
                                FolderItem.DocumentFile.DocumentType.JSON,
                                FolderItem.DocumentFile.DocumentType.UNKNOWN -> painterResource(R.drawable.ic_text_file)
                            }
                            is FolderItem.ZipFile -> painterResource(R.drawable.ic_folder_zip)
                        },
                        endIcon = when(item) {
                            is FolderItem.Folder -> painterResource(R.drawable.chevron_right)
                            else -> null
                        },
                        onClick = { onFolderItemClick(item) }
                    )
                }
            }
            if (!isReadOnly) {
                item {
                    createButtons()
                }
            }
        }
    }
}

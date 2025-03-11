package ru.geowork.photoapp.ui.screen.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.components.AppDialog
import ru.geowork.photoapp.ui.components.ButtonLarge
import ru.geowork.photoapp.ui.components.zoomable.rememberZoomState
import ru.geowork.photoapp.ui.components.zoomable.zoomable
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun Gallery(
    state: GalleryUiState,
    onUiAction: (GalleryUiAction) -> Unit
) {
    if (!state.isInitialized) return

    val coroutineScope = rememberCoroutineScope()

    var canPagerUpdateState by remember { mutableStateOf(true) }

    val previewsState = rememberLazyListState(initialFirstVisibleItemIndex = state.currentItem)

    val pagerState = rememberPagerState(
        initialPage = state.currentItem,
        pageCount = { state.items.size }
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.currentPage }.collect { page ->
            runCatching {
                if (canPagerUpdateState) {
                    onUiAction(GalleryUiAction.OnPageChanged(page))
                    val firstVisible = previewsState.layoutInfo.visibleItemsInfo.firstOrNull()
                    val lastVisible = previewsState.layoutInfo.visibleItemsInfo.lastOrNull()
                    if (firstVisible != null && lastVisible != null &&
                        page !in firstVisible.index+1..lastVisible.index - 2) {
                        previewsState.animateScrollToItem(page)
                    }
                }
            }.onFailure {
                canPagerUpdateState = true
            }
        }
    }

    if (state.isDeleteConfirmDialogShowing) {
        AppDialog(
            dismissButtonText = stringResource(R.string.cancel),
            confirmButtonText = stringResource(R.string.delete),
            title = stringResource(R.string.gallery_confirm_delete_title),
            confirmButtonColors = ButtonDefaults.buttonColors(
                backgroundColor = AppTheme.colors.systemErrorPrimary,
                contentColor = AppTheme.colors.contentConstant
            ),
            onDismiss = { onUiAction(GalleryUiAction.OnDeleteCancel) },
            onConfirm = { onUiAction(GalleryUiAction.OnDeleteConfirm) }
        ) {
            Text(
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp),
                text = stringResource(R.string.gallery_confirm_delete_text, state.items[state.currentItem].name),
                color = AppTheme.colors.contentPrimary
            )
        }
    }

    Column(modifier = Modifier.safeDrawingPadding().fillMaxSize()) {
        HorizontalPager(
            modifier = Modifier.weight(1f),
            state = pagerState
        ) { page ->
            val zoomState = rememberZoomState()
            val item = state.items[page]
            Column {
                Text(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppTheme.colors.backgroundSecondary)
                        .padding(vertical = 12.dp, horizontal = 24.dp),
                    text = "${item.name} (${item.size/1024} Кб)",
                    color = AppTheme.colors.contentPrimary,
                    style = AppTheme.typography.medium16
                )
                AsyncImage(
                    model = item.uri,
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .fillMaxSize()
                        .clipToBounds()
                        .zoomable(zoomState)
                        .onSizeChanged {
                            zoomState.setContentSize(
                                Size(
                                    width = it.width.toFloat(),
                                    height = it.height.toFloat()
                                )
                            )
                        }
                )
            }
        }
        if (state.items.size > 1) {
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AppTheme.colors.backgroundSecondary),
                state = previewsState,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp)
            ) {
                itemsIndexed(state.items) { index, item ->
                    AsyncImage(
                        model = item.uri,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(64.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .then(
                                if (state.currentItem == index) {
                                    Modifier.border(
                                        width = 2.dp,
                                        shape = RoundedCornerShape(12.dp),
                                        color = AppTheme.colors.accentPrimary
                                    )
                                } else Modifier
                            )
                            .clickable {
                                coroutineScope.launch {
                                    runCatching {
                                        canPagerUpdateState = false
                                        onUiAction(GalleryUiAction.OnPageChanged(index))
                                        pagerState.animateScrollToPage(index)
                                        canPagerUpdateState = true
                                    }.onFailure {
                                        canPagerUpdateState = true
                                    }
                                }
                            },

                    )
                }
            }
        }
        Row(modifier = Modifier
            .fillMaxWidth()
            .background(AppTheme.colors.backgroundSecondary)
            .padding(horizontal = 24.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ButtonLarge(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.systemErrorPrimary,
                    contentColor = AppTheme.colors.contentConstant
                ),
                onClick = { onUiAction(GalleryUiAction.OnDeleteClick) }
            ) {
                Text(
                    text = stringResource(id = R.string.delete),
                    style = AppTheme.typography.semibold16
                )
            }
            ButtonLarge(
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = AppTheme.colors.contentBackground,
                    contentColor = AppTheme.colors.contentPrimary
                ),
                onClick = { onUiAction(GalleryUiAction.OnCloseClick) }
            ) {
                Text(
                    text = stringResource(id = R.string.close),
                    style = AppTheme.typography.semibold16
                )
            }
        }
    }
}

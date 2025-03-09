package ru.geowork.photoapp.ui.screen.foldersync

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import ru.geowork.photoapp.ui.components.PanelHeader

@Composable
fun FolderSync(
    state: FolderSyncUiState,
    onBack: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            PanelHeader(
                title = state.title,
                onLeftButtonClick = { onBack() },
            )
        }
    }
}

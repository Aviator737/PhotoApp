package ru.geowork.photoapp.ui.screen.foldersync

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.components.PanelHeader

@Composable
fun FolderSync(
    state: FolderSyncUiState,
    onBack: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            PanelHeader(
                title = state.title,
                onLeftButtonClick = { onBack() },
            )
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 0, 0))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 1, 1))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 2, 2))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 3, 3))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 4, 4))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 5, 5))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 6, 6))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 7, 7))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 8, 8))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 9, 9))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 10, 10))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 11, 11))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 12, 12))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 13, 13))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 14, 13))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 15, 15))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 16, 16))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 17, 17))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 18, 18))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 19, 19))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 20, 20))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 21, 21))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 22, 22))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 23, 23))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 24, 24))
            Text(text = pluralStringResource(id = R.plurals.plurals_test, count = 25, 25))
        }
    }
}

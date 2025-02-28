package ru.geowork.photoapp.ui.screen.mainmenu

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ru.geowork.photoapp.R
import ru.geowork.photoapp.ui.components.ListItemWithIcon
import ru.geowork.photoapp.ui.theme.AppTheme

@Composable
fun MainMenu(
    navigateToGraves: () -> Unit,
    navigateToPoles: () -> Unit,
    navigateToSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .background(AppTheme.colors.backgroundPrimary)
            .fillMaxSize()
    ) {
        ListItemWithIcon(
            modifier = Modifier.padding(horizontal = 16.dp),
            name = stringResource(R.string.main_menu_graves),
            endIcon = painterResource(R.drawable.chevron_right),
            onClick = { navigateToGraves() }
        )
        ListItemWithIcon(
            modifier = Modifier.padding(horizontal = 16.dp),
            name = stringResource(R.string.main_menu_poles),
            endIcon = painterResource(R.drawable.chevron_right),
            onClick = { navigateToPoles() }
        )
        Spacer(modifier = Modifier.weight(1f))
        ListItemWithIcon(
            modifier = Modifier.padding(horizontal = 16.dp),
            name = stringResource(R.string.main_menu_settings),
            icon = painterResource(R.drawable.ic_settings),
            endIcon = painterResource(R.drawable.chevron_right),
            onClick = { navigateToSettings() }
        )
    }
}

@Composable
@Preview
fun MainMenuPreview() {
    AppTheme {
        MainMenu({}, {}, {})
    }
}

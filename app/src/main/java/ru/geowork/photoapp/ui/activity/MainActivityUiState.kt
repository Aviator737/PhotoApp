package ru.geowork.photoapp.ui.activity

import ru.geowork.photoapp.ui.base.UiState

data class MainActivityUiState(
    val authorized: Boolean? = null,
    val filesPermissionsNotGrantedAlertIsShowing: Boolean = false
): UiState

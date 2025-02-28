package ru.geowork.photoapp.ui.screen.auth

import ru.geowork.photoapp.ui.base.UiEvent

sealed class AuthUiEvent: UiEvent() {
    data object OpenMainMenu: AuthUiEvent()
}

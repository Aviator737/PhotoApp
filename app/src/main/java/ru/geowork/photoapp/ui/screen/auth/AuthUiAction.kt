package ru.geowork.photoapp.ui.screen.auth

import ru.geowork.photoapp.ui.base.UiAction

sealed class AuthUiAction: UiAction {
    data class OnPhotographNameInput(val value: String): AuthUiAction()
    data class OnCustomSupervisorInput(val value: String): AuthUiAction()
    data object OnCustomSupervisorInputClick: AuthUiAction()
    data object OnSelectSupervisorClick: AuthUiAction()
    data class OnSupervisorSelect(val value: String): AuthUiAction()
    data object OnDismissSupervisorSelect: AuthUiAction()
    data object OnConfirmSupervisorSelect: AuthUiAction()
    data object OnNextClick: AuthUiAction()
}

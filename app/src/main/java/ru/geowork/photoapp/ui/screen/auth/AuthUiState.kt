package ru.geowork.photoapp.ui.screen.auth

import ru.geowork.photoapp.ui.base.UiState

data class AuthUiState(
    val photographName: String = "",
    val supervisorName: String = "",
    val customSupervisorName: String = "",
    val isCustomSupervisorNameSelected: Boolean = false,
    val selectSupervisorDialog: List<Pair<String, Boolean>>? = null,
    val canGoNext: Boolean = false
): UiState

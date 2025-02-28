package ru.geowork.photoapp.ui.screen.graves

import ru.geowork.photoapp.ui.base.UiEvent

sealed class GraveyardsUiEvent: UiEvent() {
    data object NavigateBack: GraveyardsUiEvent()
    data object NavigateToCamera: GraveyardsUiEvent()
}

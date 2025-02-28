package ru.geowork.photoapp.ui.screen.camera

import dagger.hilt.android.lifecycle.HiltViewModel
import ru.geowork.photoapp.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class CameraViewModel @Inject constructor(): BaseViewModel<CameraUiState, CameraUiEvent, CameraUiAction>() {

    override val initialUiState: CameraUiState = CameraUiState()

    override fun handleCoroutineException(e: Throwable) {}

    override fun onUiAction(uiAction: CameraUiAction) {
        when (uiAction) {
            CameraUiAction.NavigateBack -> handleNavigateBack()
        }
    }

    private fun handleNavigateBack() {
        sendUiEvent(CameraUiEvent.NavigateBack)
    }
}

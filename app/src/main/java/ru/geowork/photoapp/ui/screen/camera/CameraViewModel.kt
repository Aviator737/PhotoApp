package ru.geowork.photoapp.ui.screen.camera

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.geowork.photoapp.data.DataStoreRepository
import ru.geowork.photoapp.ui.base.BaseViewModel
import javax.inject.Inject
import kotlin.math.max

@HiltViewModel
class CameraViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
): BaseViewModel<CameraUiState, CameraUiEvent, CameraUiAction>() {

    override val initialUiState: CameraUiState = CameraUiState()

    init {
        initSettings()
    }

    override fun handleCoroutineException(e: Throwable) {}

    override fun onUiAction(uiAction: CameraUiAction) {
        when (uiAction) {
            is CameraUiAction.OnZoomLevelsResolved -> handleOnZoomLevelsResolved(uiAction.minZoom, uiAction.maxZoom)
            is CameraUiAction.OnZoomSelected -> handleOnZoomSelected(uiAction.value)
            CameraUiAction.SwitchExposureMenu -> handleSwitchExposureMenu()
            CameraUiAction.SwitchGrid -> handleSwitchGrid()
            CameraUiAction.SwitchHDR -> handleSwitchHdr()
            CameraUiAction.NavigateBack -> handleNavigateBack()
        }
    }

    private fun handleOnZoomLevelsResolved(minZoom: Float, maxZoom: Float) = viewModelScopeErrorHandled.launch {
        val savedZoom = dataStoreRepository.getCameraZoom() ?: 1f
        val zoomLevels = mutableListOf<Pair<Float, Boolean>>().apply {
            add(Pair(minZoom, savedZoom == minZoom))
            if (minZoom != 1f) add(Pair(1f, savedZoom == 1f))
            if (maxZoom > 2f) add(Pair(2f, savedZoom == 2f))
            if (maxZoom > 5f) add(Pair(5f, savedZoom == 5f))
            add(Pair(maxZoom, savedZoom == maxZoom))
        }
        updateUiState {
            it.copy(
                zoomLevels = zoomLevels
            )
        }
    }

    private fun handleOnZoomSelected(value: Float) = viewModelScopeErrorHandled.launch {
        updateUiState { state -> state.copy(zoomLevels = state.zoomLevels.map { Pair(it.first, it.first == value) }) }
        dataStoreRepository.saveCameraZoom(value)
    }

    private fun handleSwitchExposureMenu() {

    }

    private fun handleSwitchGrid() = viewModelScopeErrorHandled.launch {
        val showGrid = !uiState.value.showGrid
        dataStoreRepository.saveCameraShowGrid(showGrid)
        updateUiState { it.copy(showGrid = showGrid) }
    }

    private fun handleSwitchHdr() = viewModelScopeErrorHandled.launch {
        val isHdrOn = !uiState.value.isHdrOn
        dataStoreRepository.saveCameraHdrOn(isHdrOn)
        updateUiState { it.copy(isHdrOn = isHdrOn) }
    }

    private fun handleNavigateBack() {
        sendUiEvent(CameraUiEvent.NavigateBack)
    }

    private fun initSettings() = viewModelScopeErrorHandled.launch {
        val showGrid = dataStoreRepository.getCameraShowGrid() ?: false
        val isHdrOn = dataStoreRepository.getCameraHdrOn() ?: false
        val exposureCompensationIndex = dataStoreRepository.getCameraExposureCompensationIndex()

        updateUiState {
            it.copy(
                isHdrOn = isHdrOn,
                exposureCompensationIndex = exposureCompensationIndex,
                showGrid = showGrid
            )
        }
    }
}

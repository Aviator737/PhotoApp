package ru.geowork.photoapp.ui.screen.settings

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalZeroShutterLag
import androidx.camera.core.ImageCapture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.geowork.photoapp.data.DataStoreRepository
import ru.geowork.photoapp.data.sync.SyncRepository
import ru.geowork.photoapp.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val syncRepository: SyncRepository
): BaseViewModel<SettingsUiState, SettingsUiEvent, SettingsUiAction>() {

    override val initialUiState: SettingsUiState = SettingsUiState()

    private var setImageQualityJob: Job? = null

    init {
        initSettings()
    }

    override fun handleCoroutineException(e: Throwable) {}

    override fun onUiAction(uiAction: SettingsUiAction) {
        when(uiAction) {
            is SettingsUiAction.OnImageMaxSizeInput -> handleOnImageMaxSizeInput(uiAction.value)
            is SettingsUiAction.OnCaptureModeSelected -> handleOnCaptureModeSelected(uiAction.value)
            SettingsUiAction.OnCaptureModeOpenChooser -> handleOnCaptureModeOpenChooser()
            SettingsUiAction.OnCaptureModeSelectorConfirm -> handleOnCaptureModeSelectorConfirm()
            SettingsUiAction.OnCaptureModeSelectorDismiss -> handleOnCaptureModeSelectorDismiss()
            SettingsUiAction.OnDeleteSyncStateStoreClick -> handleOnDeleteSyncStateStoreClick()
        }
    }

    private fun handleOnImageMaxSizeInput(value: String) {
        updateUiState { it.copy(maxImageSize = value) }
        setImageQualityJob?.cancel()
        setImageQualityJob = viewModelScopeErrorHandled.launch {
            delay(1000)
            val fixValue = value.toIntOrNull() ?: dataStoreRepository.getMaxImageSize()
            dataStoreRepository.saveMaxImageSize(fixValue)
            updateUiState { it.copy(maxImageSize = fixValue.toString()) }
        }
    }

    private fun handleOnCaptureModeSelectorConfirm() = viewModelScopeErrorHandled.launch {
        val selected = uiState.value.captureModeState.chooser?.selected ?: uiState.value.captureModeState.captureMode
        selected?.let {
            dataStoreRepository.saveCaptureMode(it)
        }
        updateUiState { it.copy(captureModeState = it.captureModeState.copy(captureMode = selected, chooser = null)) }
    }

    @OptIn(ExperimentalZeroShutterLag::class)
    private fun handleOnCaptureModeOpenChooser() {
        updateUiState {
            it.copy(captureModeState = it.captureModeState.copy(chooser = SettingsUiState.CaptureModeState.Chooser(
                selected = it.captureModeState.captureMode,
                options = listOf(
                    ImageCapture.CAPTURE_MODE_MAXIMIZE_QUALITY,
                    ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY,
                    ImageCapture.CAPTURE_MODE_ZERO_SHUTTER_LAG
                )
            )))
        }
    }

    private fun handleOnCaptureModeSelected(value: Int) =
        updateUiState { it.copy(captureModeState = it.captureModeState.copy(chooser = it.captureModeState.chooser?.copy(selected = value))) }

    private fun handleOnCaptureModeSelectorDismiss() =
        updateUiState { it.copy(captureModeState = it.captureModeState.copy(chooser = null)) }

    private fun handleOnDeleteSyncStateStoreClick() = syncRepository.deleteSyncStateStore()

    private fun initSettings() = viewModelScopeErrorHandled.launch {
        val imageQuality = dataStoreRepository.getMaxImageSize()
        val captureMode = dataStoreRepository.getCaptureMode()
        updateUiState {
            it.copy(
                maxImageSize = imageQuality.toString(),
                captureModeState = it.captureModeState.copy(
                    captureMode = captureMode
                )
            )
        }
    }
}

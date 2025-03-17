package ru.geowork.photoapp.ui.screen.settings

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.geowork.photoapp.data.DataStoreRepository
import ru.geowork.photoapp.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
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

    private fun initSettings() = viewModelScopeErrorHandled.launch {
        val imageQuality = dataStoreRepository.getMaxImageSize()
        updateUiState { it.copy(maxImageSize = imageQuality.toString()) }
    }
}

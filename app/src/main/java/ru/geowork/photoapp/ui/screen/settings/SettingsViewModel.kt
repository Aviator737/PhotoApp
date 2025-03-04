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
            is SettingsUiAction.OnImageQualityInput -> handleOnImageQualityInput(uiAction.value)
        }
    }

    private fun handleOnImageQualityInput(value: String) {
        updateUiState { it.copy(imageQuality = value) }
        val intValue = value.toIntOrNull()
        setImageQualityJob?.cancel()
        setImageQualityJob = viewModelScopeErrorHandled.launch {
            val fixValue = if (intValue == null || intValue !in 10..100) 80 else intValue
            dataStoreRepository.saveImageQuality(fixValue)
            delay(1500)
            updateUiState { it.copy(imageQuality = fixValue.toString()) }
        }
    }

    private fun initSettings() = viewModelScopeErrorHandled.launch {
        val imageQuality = dataStoreRepository.getImageQuality()
        updateUiState { it.copy(imageQuality = imageQuality.toString()) }
    }
}

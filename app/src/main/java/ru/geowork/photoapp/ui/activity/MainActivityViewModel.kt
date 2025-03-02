package ru.geowork.photoapp.ui.activity

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.geowork.photoapp.data.DataStoreRepository
import ru.geowork.photoapp.ui.base.BaseViewModel
import ru.geowork.photoapp.ui.base.UiEvent
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
): BaseViewModel<MainActivityUiState, UiEvent, MainActivityUiAction>() {

    override val initialUiState: MainActivityUiState = MainActivityUiState()

    init {
        checkAccount()
    }

    override fun handleCoroutineException(e: Throwable) {}

    override fun onUiAction(uiAction: MainActivityUiAction) {
        when(uiAction) {
            MainActivityUiAction.OnFilePermissionsNotGranted -> handleOnFilePermissionsNotGranted()
            MainActivityUiAction.CloseAlerts -> handleCloseAlerts()
        }
    }

    private fun checkAccount() = viewModelScopeErrorHandled.launch {
        val photographName = dataStoreRepository.getPhotographName()
        val supervisorName = dataStoreRepository.getSupervisorName()
        updateUiState {
            it.copy(
                authorized = !photographName.isNullOrEmpty() && !supervisorName.isNullOrEmpty()
            )
        }
    }

    private fun handleOnFilePermissionsNotGranted() {
        updateUiState { it.copy(filesPermissionsNotGrantedAlertIsShowing = true) }
    }

    private fun handleCloseAlerts() {
        updateUiState { it.copy(filesPermissionsNotGrantedAlertIsShowing = false) }
    }
}

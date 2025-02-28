package ru.geowork.photoapp.ui.activity

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.geowork.photoapp.data.AccountRepository
import ru.geowork.photoapp.ui.base.BaseViewModel
import ru.geowork.photoapp.ui.base.UiEvent
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val accountRepository: AccountRepository
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
        val account = accountRepository.getAccount()
        updateUiState {
            it.copy(
                authorized = !account.photographName.isNullOrEmpty() && !account.supervisorName.isNullOrEmpty()
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

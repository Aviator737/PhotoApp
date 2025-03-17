package ru.geowork.photoapp.ui.screen.auth

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import ru.geowork.photoapp.data.DataStoreRepository
import ru.geowork.photoapp.ui.base.BaseViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val dataStoreRepository: DataStoreRepository
): BaseViewModel<AuthUiState, AuthUiEvent, AuthUiAction>() {

    override val initialUiState: AuthUiState = AuthUiState()

    init {
        initValues()
    }

    override fun handleCoroutineException(e: Throwable) {}

    override fun onUiAction(uiAction: AuthUiAction) {
        when(uiAction) {
            is AuthUiAction.OnPhotographNameInput -> handleOnPhotographNameInput(uiAction.value)
            is AuthUiAction.OnCustomSupervisorInput -> handleOnCustomSupervisorInput(uiAction.value)
            AuthUiAction.OnCustomSupervisorInputClick -> handleOnCustomSupervisorInputClick()
            AuthUiAction.OnSelectSupervisorClick -> handleOnSelectSupervisorClick()
            is AuthUiAction.OnSupervisorSelect -> handleOnSupervisorSelect(uiAction.value)
            AuthUiAction.OnDismissSupervisorSelect -> handleOnDismissSupervisorSelect()
            AuthUiAction.OnConfirmSupervisorSelect -> handleOnConfirmSupervisorSelect()
            AuthUiAction.OnNextClick -> handleOnNextClick()
        }
    }

    private fun handleOnPhotographNameInput(value: String) {
        updateUiState { it.copy(photographName = value) }
        viewModelScopeErrorHandled.launch {
            dataStoreRepository.savePhotographName(value)
        }
        checkCanGoNext()
    }

    private fun handleOnCustomSupervisorInput(value: String) {
        updateUiState { it.copy(customSupervisorName = value) }
    }

    private fun handleOnCustomSupervisorInputClick() {
        updateUiState {
            it.copy(
                isCustomSupervisorNameSelected = true,
                selectSupervisorDialog = it.selectSupervisorDialog?.map { pair ->
                    pair.first to false
                }
            )
        }
    }

    private fun handleOnSelectSupervisorClick() {
        viewModelScopeErrorHandled.launch {
            val savedSupervisor = dataStoreRepository.getSupervisorName()
            val isCustomSupervisor = SUPERVISORS.firstOrNull { it == savedSupervisor } == null
            updateUiState { state ->
                state.copy(
                    isCustomSupervisorNameSelected = isCustomSupervisor && !savedSupervisor.isNullOrEmpty(),
                    customSupervisorName = savedSupervisor.takeIf { isCustomSupervisor }.orEmpty(),
                    selectSupervisorDialog = SUPERVISORS.map {
                        it to (savedSupervisor == it)
                    }
                )
            }
        }
    }

    private fun handleOnSupervisorSelect(value: String) {
        updateUiState {
            it.copy(
                isCustomSupervisorNameSelected = false,
                selectSupervisorDialog = it.selectSupervisorDialog?.map { pair ->
                    pair.first to (value == pair.first)
                }
            )
        }
    }

    private fun handleOnDismissSupervisorSelect() {
        updateUiState { it.copy(selectSupervisorDialog = null) }
    }

    private fun handleOnConfirmSupervisorSelect() {
        val selectedSupervisor = if (uiState.value.isCustomSupervisorNameSelected) {
            uiState.value.customSupervisorName
        } else {
            uiState.value.selectSupervisorDialog?.firstOrNull { pair -> pair.second }?.first.orEmpty()
        }

        updateUiState {
            it.copy(
                supervisorName = selectedSupervisor,
                selectSupervisorDialog = null
            )
        }
        viewModelScopeErrorHandled.launch {
            dataStoreRepository.saveSupervisorName(selectedSupervisor)
            checkCanGoNext()
        }
    }

    private fun handleOnNextClick() = viewModelScopeErrorHandled.launch {
        sendUiEvent(AuthUiEvent.OpenMainMenu)
    }

    private fun checkCanGoNext() {
        val photographNames = uiState.value.photographName.trim().split(" ")
        val canGoNext =
            photographNames.size == 2 &&
            photographNames[0].length >= 2 &&
            photographNames[1].length >= 2 &&
            uiState.value.supervisorName.isNotEmpty()
        updateUiState { it.copy(canGoNext = canGoNext) }
    }

    private fun initValues() = viewModelScopeErrorHandled.launch {
        val photographName = dataStoreRepository.getPhotographName()
        val supervisorName = dataStoreRepository.getSupervisorName()
        updateUiState {
            it.copy(
                photographName = photographName.orEmpty(),
                supervisorName = supervisorName.orEmpty()
            )
        }
        checkCanGoNext()
    }

    companion object {
        private val SUPERVISORS = listOf("Михаил Кулешов", "Сергей Новиков", "Кирилл Банкович")
    }
}

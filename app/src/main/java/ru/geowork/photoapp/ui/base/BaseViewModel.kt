package ru.geowork.photoapp.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.plus

abstract class BaseViewModel<UIS : UiState, UIE : UiEvent, UIA : UiAction> : ViewModel() {

    protected abstract val initialUiState: UIS
    private val _uiState: MutableStateFlow<UIS> by lazy { MutableStateFlow(initialUiState) }
    val uiState: StateFlow<UIS> by lazy { _uiState.asStateFlow() }

    protected fun updateUiState(function: (UIS) -> UIS) {
        _uiState.update { function.invoke(it) }
    }

    private val _uiEvents: MutableStateFlow<List<UIE>> = MutableStateFlow(emptyList())
    val uiEvents: StateFlow<List<UIE>> = _uiEvents.asStateFlow()

    protected fun sendUiEvent(uiEvent: UIE) {
        _uiEvents.update { it.plus(uiEvent) }
    }

    fun onUiEventHandled(handledUiEvent: UIE) {
        _uiEvents.update {
            it.filterNot { uiEvent ->
                uiEvent.uniqueId == handledUiEvent.uniqueId
            }
        }
    }

    abstract fun onUiAction(uiAction: UIA)

    protected abstract fun handleCoroutineException(e: Throwable)

    private val exceptionHandler = CoroutineExceptionHandler { _, exception ->
        if (exception !is CancellationException) handleCoroutineException(exception)
    }

    protected val viewModelScopeErrorHandled = viewModelScope + exceptionHandler
}

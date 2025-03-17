package ru.geowork.photoapp.ui.screen.upload

import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.geowork.photoapp.data.FilesRepository
import ru.geowork.photoapp.ui.base.BaseViewModel

@HiltViewModel(assistedFactory = UploadAssistedFactory::class)
class UploadViewModel @AssistedInject constructor(
    private val filesRepository: FilesRepository,
    @Assisted private val payload: UploadPayload
): BaseViewModel<UploadUiState, UploadUiEvent, UploadUiAction>() {
    override val initialUiState: UploadUiState = UploadUiState()

    override fun handleCoroutineException(e: Throwable) {}

    override fun onUiAction(uiAction: UploadUiAction) {

    }
}

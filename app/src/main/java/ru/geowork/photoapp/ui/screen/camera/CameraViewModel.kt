package ru.geowork.photoapp.ui.screen.camera

import android.net.Uri
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import ru.geowork.photoapp.data.DataStoreRepository
import ru.geowork.photoapp.data.FilesRepository
import ru.geowork.photoapp.model.FolderItem
import ru.geowork.photoapp.ui.base.BaseViewModel
import ru.geowork.photoapp.ui.screen.gallery.GalleryPayload

@HiltViewModel(assistedFactory = CameraAssistedFactory::class)
class CameraViewModel @AssistedInject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val filesRepository: FilesRepository,
    @Assisted private val payload: CameraPayload
): BaseViewModel<CameraUiState, CameraUiEvent, CameraUiAction>() {

    private var isSettingsInitialized: Boolean = false
    private var isZoomLevelsInitialized: Boolean = false
    private var isExposureInitialized: Boolean = false
    private val isInitialized
        get() = isSettingsInitialized && isZoomLevelsInitialized && isExposureInitialized

    private var saveExposureJob: Job? = null

    override val initialUiState: CameraUiState = CameraUiState()

    init {
        initSettings()
    }

    override fun handleCoroutineException(e: Throwable) {
        println(e)
    }

    override fun onUiAction(uiAction: CameraUiAction) {
        when (uiAction) {
            CameraUiAction.OnUpdateFolderItems -> getFolderItems()

            is CameraUiAction.OnZoomLevelsResolved -> handleOnZoomLevelsResolved(
                uiAction.minZoom,
                uiAction.maxZoom
            )

            is CameraUiAction.OnExposureResolved ->
                handleOnExposureResolved(
                    uiAction.isSupported,
                    uiAction.default,
                    uiAction.step,
                    uiAction.min,
                    uiAction.max
                )

            is CameraUiAction.OnExposureStepSelected -> handleOnExposureStepSelected(uiAction.step)

            is CameraUiAction.OnZoomSelected -> handleOnZoomSelected(uiAction.value)
            CameraUiAction.SwitchExposureMenu -> handleSwitchExposureMenu()
            CameraUiAction.SwitchGrid -> handleSwitchGrid()

            CameraUiAction.OnTakePhotoClick -> handleOnTakePhotoClick()
            is CameraUiAction.OnPhotoTaken -> handleOnPhotoTaken(uiAction.uri)

            is CameraUiAction.OnPhotoClick -> handleOnPhotoClick(uiAction.position)

            CameraUiAction.NavigateBack -> handleNavigateBack()
        }
    }

    private fun handleOnZoomLevelsResolved(minZoom: Float, maxZoom: Float) = viewModelScopeErrorHandled.launch {
        val savedZoom = dataStoreRepository.getCameraZoom() ?: 1f
        val zoomLevels = mutableListOf<Pair<Float, Boolean>>().apply {
            add(Pair(minZoom, savedZoom == minZoom))
            if (minZoom != 1f) add(Pair(1f, savedZoom == 1f))
            if (maxZoom > 2f) add(Pair(2f, savedZoom == 2f))
            if (maxZoom > 3f) add(Pair(3f, savedZoom == 3f))
            if (maxZoom >= 5f) add(Pair(5f, savedZoom == 5f))
            if (maxZoom > 5f) add(Pair(maxZoom, savedZoom == maxZoom))
        }
        isZoomLevelsInitialized = true
        updateUiState { it.copy(isInitialized = isInitialized, zoomLevels = zoomLevels) }
    }

    private fun handleOnExposureResolved(
        isSupported: Boolean,
        default: Int,
        step: Float,
        min: Int,
        max: Int
    ) = viewModelScopeErrorHandled.launch {
        val selectedIndex = dataStoreRepository.getCameraExposureCompensationIndex() ?: default

        val steps = mutableListOf<CameraUiState.ExposureState.Step>()
        var index = min

        while (index <= max) {
            steps.add(
                CameraUiState.ExposureState.Step(
                    index = index,
                    value = step * index
                )
            )
            index += 1
        }

        isExposureInitialized = true

        updateUiState { state ->
            state.copy(
                isInitialized = isInitialized,
                exposureState = CameraUiState.ExposureState(
                    isSupported = isSupported,
                    selectedStep = steps.firstOrNull { it.index == selectedIndex },
                    steps = steps,
                )
            )
        }
    }

    private fun handleOnExposureStepSelected(step: CameraUiState.ExposureState.Step) {
        saveExposureJob?.cancel()
        updateUiState { it.copy(exposureState = it.exposureState.copy(selectedStep = step)) }
        saveExposureJob = viewModelScopeErrorHandled.launch {
            delay(1000)
            dataStoreRepository.saveCameraExposure(step.index)
        }
    }

    private fun handleOnZoomSelected(value: Float) = viewModelScopeErrorHandled.launch {
        updateUiState { state -> state.copy(zoomLevels = state.zoomLevels.map { Pair(it.first, it.first == value) }) }
        dataStoreRepository.saveCameraZoom(value)
    }

    private fun handleSwitchExposureMenu() {
        updateUiState { it.copy(exposureState = it.exposureState.copy(isVisible = !it.exposureState.isVisible)) }
    }

    private fun handleSwitchGrid() = viewModelScopeErrorHandled.launch {
        val showGrid = !uiState.value.showGrid
        dataStoreRepository.saveCameraShowGrid(showGrid)
        updateUiState { it.copy(showGrid = showGrid) }
    }

    private fun handleOnTakePhotoClick() = viewModelScopeErrorHandled.launch {
        val photosCount = uiState.value.items.size
        val name = "${payload.name}_$photosCount"
        val imageItem = FolderItem.ImageFile(name = name, parentFolder = payload.savePath)
        filesRepository.createFolderItem(imageItem, payload.savePath)?.let { uri ->
            filesRepository.openOutputStream(uri)?.let { stream ->
                updateUiState { it.copy(takePhoto = CameraUiState.TakePhotoState(uri, stream)) }
            }
        }
    }

    private fun handleOnPhotoTaken(uri: Uri?) = viewModelScopeErrorHandled.launch {
        updateUiState { it.copy(takePhoto = null) }
        getFolderItems()
        if (uri == null) return@launch
        val isCompressed = filesRepository.compressImage(uri)
        if (isCompressed) getFolderItems()
    }

    private fun handleOnPhotoClick(position: Int) {
        sendUiEvent(CameraUiEvent.NavigateToGallery(GalleryPayload(position, payload.savePath)))
    }

    private fun handleNavigateBack() {
        sendUiEvent(CameraUiEvent.NavigateBack)
    }

    private fun initSettings() = viewModelScopeErrorHandled.launch {
        val showGrid = dataStoreRepository.getCameraShowGrid() ?: false
        isSettingsInitialized = true
        updateUiState {
            it.copy(
                isInitialized = isInitialized,
                showGrid = showGrid
            )
        }
    }

    private fun getFolderItems() = viewModelScopeErrorHandled.launch {
        val folderItems = filesRepository.getFolderItems(payload.savePath).filterIsInstance<FolderItem.ImageFile>()
        updateUiState { it.copy(items = folderItems) }
    }
}

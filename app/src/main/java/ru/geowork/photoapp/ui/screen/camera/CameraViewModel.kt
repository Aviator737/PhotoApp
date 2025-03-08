package ru.geowork.photoapp.ui.screen.camera

import android.graphics.Bitmap
import androidx.exifinterface.media.ExifInterface
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
import ru.geowork.photoapp.util.rotateIfRequired

@HiltViewModel(assistedFactory = CameraAssistedFactory::class)
class CameraViewModel @AssistedInject constructor(
    private val dataStoreRepository: DataStoreRepository,
    private val filesRepository: FilesRepository,
    @Assisted private val payload: CameraPayload
): BaseViewModel<CameraUiState, CameraUiEvent, CameraUiAction>() {

    private var saveExposureJob: Job? = null

    override val initialUiState: CameraUiState = CameraUiState()

    init {
        initSettings()
        getFolderItems()
    }

    override fun handleCoroutineException(e: Throwable) {
        throw RuntimeException("test crash")
    }

    override fun onUiAction(uiAction: CameraUiAction) {
        when (uiAction) {
            is CameraUiAction.OnZoomLevelsResolved -> handleOnZoomLevelsResolved(uiAction.minZoom, uiAction.maxZoom)
            is CameraUiAction.OnExposureResolved ->
                handleOnExposureCompensationRangeResolved(uiAction.isSupported, uiAction.default, uiAction.step, uiAction.min, uiAction.max)
            is CameraUiAction.OnExposureStepSelected -> handleOnExposureStepSelected(uiAction.step)

            is CameraUiAction.OnZoomSelected -> handleOnZoomSelected(uiAction.value)
            CameraUiAction.SwitchExposureMenu -> handleSwitchExposureMenu()
            CameraUiAction.SwitchGrid -> handleSwitchGrid()
            is CameraUiAction.OnPhotoTaken -> handleOnPhotoTaken(uiAction.bitmap, uiAction.exif)
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
        updateUiState { it.copy(zoomLevels = zoomLevels) }
    }

    private fun handleOnExposureCompensationRangeResolved(
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
            index += 2
        }

        updateUiState { state ->
            state.copy(
                exposureState = CameraUiState.ExposureState(
                    isSupported = isSupported,
                    selectedStep = steps.firstOrNull { it.index == selectedIndex },
                    steps = steps
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

    private fun handleOnPhotoTaken(bitmap: Bitmap, exif: ExifInterface) = viewModelScopeErrorHandled.launch {
        val photosCount = uiState.value.items.size
        val name = payload.savePath.replace('/', '_') + "_$photosCount"
        val rotatedBitmap = bitmap.rotateIfRequired(exif)
        val imageItem = FolderItem.ImageFile(name = name, path = payload.savePath)
        filesRepository.createFolderItem(imageItem)?.let { uri ->
            filesRepository.saveBitmapToUri(rotatedBitmap, uri)
        }
        getFolderItems()
    }

    private fun handleNavigateBack() {
        sendUiEvent(CameraUiEvent.NavigateBack)
    }

    private fun initSettings() = viewModelScopeErrorHandled.launch {
        val imageQuality = dataStoreRepository.getImageQuality()
        val showGrid = dataStoreRepository.getCameraShowGrid() ?: false

        updateUiState {
            it.copy(
                imageQuality = imageQuality,
                showGrid = showGrid
            )
        }
    }

    private fun getFolderItems() = viewModelScopeErrorHandled.launch {
        val folderItems = filesRepository.getFolderItems(payload.savePath)
        updateUiState { it.copy(items = folderItems) }
    }
}

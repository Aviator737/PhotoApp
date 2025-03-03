package ru.geowork.photoapp.ui.screen.camera

import dagger.assisted.AssistedFactory

@AssistedFactory
interface CameraAssistedFactory {
    fun create(payload: CameraPayload): CameraViewModel
}

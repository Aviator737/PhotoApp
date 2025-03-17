package ru.geowork.photoapp.ui.screen.upload

import dagger.assisted.AssistedFactory

@AssistedFactory
interface UploadAssistedFactory {
    fun create(payload: UploadPayload): UploadViewModel
}

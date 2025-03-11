package ru.geowork.photoapp.ui.screen.gallery

import dagger.assisted.AssistedFactory

@AssistedFactory
interface GalleryAssistedFactory {
    fun create(payload: GalleryPayload): GalleryViewModel
}

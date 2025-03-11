package ru.geowork.photoapp.ui.screen.gallery

import kotlinx.serialization.Serializable

@Serializable
data class GalleryPayload(
    val position: Int,
    val path: String
)

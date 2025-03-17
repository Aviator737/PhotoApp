package ru.geowork.photoapp.ui.screen.upload

import kotlinx.serialization.Serializable

@Serializable
data class UploadPayload(
    val path: String
)

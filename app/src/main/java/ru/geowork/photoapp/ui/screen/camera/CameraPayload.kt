package ru.geowork.photoapp.ui.screen.camera

import kotlinx.serialization.Serializable

@Serializable
data class CameraPayload(
    val savePath: String
)

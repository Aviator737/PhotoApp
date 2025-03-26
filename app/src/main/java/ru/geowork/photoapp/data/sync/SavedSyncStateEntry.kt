package ru.geowork.photoapp.data.sync

import kotlinx.serialization.Serializable

@Serializable
data class SavedSyncStateEntry(
    val path: String,
    val state: String
)

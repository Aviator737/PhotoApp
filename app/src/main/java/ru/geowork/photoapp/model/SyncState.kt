package ru.geowork.photoapp.model

import ru.geowork.photoapp.data.sync.SavedSyncState

sealed class SyncState {
    data object NotReady: SyncState()
    data object Ready: SyncState()
    data object Uploaded: SyncState()
    data class Failed(val throwable: Throwable): SyncState()

    data class Archiving(val value: Float): SyncState()

    data object Connecting: SyncState()
    data class Uploading(val value: Float): SyncState()

    companion object {
        fun fromSavedSyncState(saved: SavedSyncState) = when(saved) {
            SavedSyncState.NOT_READY -> NotReady
            SavedSyncState.READY -> Ready
            SavedSyncState.UPLOADED -> Uploaded
            SavedSyncState.FAILED -> Failed(Exception())
        }
    }
}

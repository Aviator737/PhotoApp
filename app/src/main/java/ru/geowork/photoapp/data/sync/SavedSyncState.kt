package ru.geowork.photoapp.data.sync

enum class SavedSyncState(val value: String) {
    NOT_READY("not_ready"), READY("ready"), UPLOADED("uploaded"), FAILED("failed");

    companion object {
        fun fromValue(value: String) = SavedSyncState.entries.firstOrNull { value == it.value } ?: NOT_READY
    }
}

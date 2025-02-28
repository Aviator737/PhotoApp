package ru.geowork.photoapp.ui.base

import java.util.UUID

open class UiEvent {
    val uniqueId: String = UUID.randomUUID().toString()
}

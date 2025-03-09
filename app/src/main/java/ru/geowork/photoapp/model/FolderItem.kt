package ru.geowork.photoapp.model

import android.net.Uri

sealed class FolderItem {
    abstract val name: String
    abstract val path: String

    data class TextFile(
        override val name: String = "",
        override val path: String = ""
    ): FolderItem()

    data class ImageFile(
        override val name: String = "",
        override val path: String = "",
        val uri: Uri? = null
    ): FolderItem()

    data class Folder(
        override val name: String = "",
        val visibleName: String? = null,
        override val path: String = ""
    ): FolderItem()

    data class Unknown(
        override val name: String = "",
        override val path: String = ""
    ): FolderItem()

    data class PhotoRow(
        override val name: String = "",
        override val path: String = "",
        val items: List<FolderItem> = listOf()
    ): FolderItem()
}

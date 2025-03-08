package ru.geowork.photoapp.model

sealed class FolderItem {
    abstract val name: String
    abstract val path: String
    abstract val fullPath: String

    data class TextFile(
        override val name: String = "",
        override val path: String = "",
        override val fullPath: String = ""
    ): FolderItem()

    data class ImageFile(
        override val name: String = "",
        override val path: String = "",
        override val fullPath: String = ""
    ): FolderItem()

    data class Folder(
        override val name: String = "",
        val visibleName: String? = null,
        override val path: String = "",
        override val fullPath: String = ""
    ): FolderItem()

    data class Unknown(
        override val name: String = "",
        override val path: String = "",
        override val fullPath: String = ""
    ): FolderItem()

    data class PhotoRow(
        override val name: String = "",
        override val path: String = "",
        override val fullPath: String = "",
        val items: List<FolderItem> = listOf()
    ): FolderItem()
}

package ru.geowork.photoapp.model

sealed class FolderItem(
    open val name: String,
    open val path: String
) {
    data class TextFile(
        override val name: String = "",
        override val path: String = ""
    ): FolderItem(name = name, path = path)

    data class ImageFile(
        override val name: String = "",
        override val path: String = ""
    ): FolderItem(name = name, path = path)

    data class Folder(
        override val name: String = "",
        override val path: String = ""
    ): FolderItem(name = name, path = path)

    data class Unknown(
        override val name: String = "",
        override val path: String = ""
    ): FolderItem(name = name, path = path)

    data class PhotoRow(
        override val name: String = "",
        override val path: String = ""
    ): FolderItem(name = name, path = path)
}

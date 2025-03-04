package ru.geowork.photoapp.model

sealed class FolderItem(
    open val name: String,
    open val path: String, //все что после папки аккаунта
    open val fullPath: String
) {
    data class TextFile(
        override val name: String = "",
        override val path: String = "",
        override val fullPath: String = ""
    ): FolderItem(name = name, path = path, fullPath = fullPath)

    data class ImageFile(
        override val name: String = "",
        override val path: String = "",
        override val fullPath: String = ""
    ): FolderItem(name = name, path = path, fullPath = fullPath)

    data class Folder(
        override val name: String = "",
        override val path: String = "",
        override val fullPath: String = ""
    ): FolderItem(name = name, path = path, fullPath = fullPath)

    data class Unknown(
        override val name: String = "",
        override val path: String = "",
        override val fullPath: String = ""
    ): FolderItem(name = name, path = path, fullPath = fullPath)

    data class PhotoRow(
        override val name: String = "",
        override val path: String = "",
        override val fullPath: String = "",
        val items: List<FolderItem> = listOf()
    ): FolderItem(name = name, path = path, fullPath = fullPath)
}

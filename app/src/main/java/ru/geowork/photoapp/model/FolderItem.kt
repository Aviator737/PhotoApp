package ru.geowork.photoapp.model

import android.net.Uri

sealed class FolderItem {
    abstract val id: String
    abstract val name: String
    abstract val uri: Uri?

    data class DocumentFile(
        override val id: String = "",
        override val name: String = "",
        override val uri: Uri? = null,
        val type: DocumentType = DocumentType.UNKNOWN
    ): FolderItem() {
        enum class DocumentType(val extension: String) {
            TXT("txt"), PDF("pdf"), UNKNOWN("txt");

            companion object {
                fun getTypeFromExtension(ext: String) = entries.firstOrNull { ext == it.extension } ?: UNKNOWN
            }
        }
    }

    data class ImageFile(
        override val id: String = "",
        override val name: String = "",
        override val uri: Uri? = null,
        val parentFolder: String = "",
        val size: Long = 0,
        val type: ImageType = ImageType.UNKNOWN
    ): FolderItem() {
        enum class ImageType(val extension: String) {
            JPG("jpg"), JPEG("jpeg"), PNG("png"), WEBP("webp"), UNKNOWN("jpg");

            companion object {
                fun getTypeFromExtension(ext: String) = ImageType.entries.firstOrNull { ext == it.extension } ?: UNKNOWN
            }
        }
    }

    data class Folder(
        override val id: String = "",
        override val name: String = "",
        override val uri: Uri? = null,
        val relativePath: String = "", //path without /app_folder_name/account_folder/
        val childItems: List<FolderItem>? = null
    ): FolderItem()
}

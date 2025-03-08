package ru.geowork.photoapp.util

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import ru.geowork.photoapp.model.FolderItem
import java.io.File

private const val APP_FOLDER_NAME = "GWApp"

private val APP_FILES_DIRECTORY =
    "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path}/$APP_FOLDER_NAME"

private const val MIME_TYPE_FOLDER = "vnd.android.document/directory"
private const val MIME_TYPE_TEXT = "text/plain"
private const val MIME_TYPE_IMAGE_JPEG = "image/jpeg"
private const val MIME_TYPE_IMAGE_PNG = "image/png"
private const val MIME_TYPE_PDF = "application/pdf"

fun Context.createFileInDocuments(fileName: String, path: String, ext: String): Uri? {
    val mimeType = when (ext) {
        ".txt" -> MIME_TYPE_TEXT
        ".jpg" -> MIME_TYPE_IMAGE_JPEG
        ".png" -> MIME_TYPE_IMAGE_PNG
        else -> MIME_TYPE_FOLDER
    }
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOCUMENTS}/$APP_FOLDER_NAME/$path${if (ext.isEmpty()) "/$fileName" else ""}")
    }
    return contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
}

fun Context.saveBitmapToUri(bitmap: Bitmap, uri: Uri, quality: Int = 100) {
    contentResolver.openOutputStream(uri)?.use { outputStream ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
    }
}

fun Context.getFilesFromDocuments(path: String, cleanPath: String): List<FolderItem> {
    val folderItems = mutableListOf<FolderItem>()
    val fullPath = File("$APP_FILES_DIRECTORY/$path")
    if (fullPath.exists()) {
        fullPath.listFiles()?.forEach { file ->
            val type = getFileType(file)
            val item = when(type) {
                null ->
                    FolderItem.Folder(name = file.name, path = cleanPath, fullPath = file.path)
                MIME_TYPE_IMAGE_JPEG, MIME_TYPE_IMAGE_PNG, MIME_TYPE_PDF ->
                    FolderItem.ImageFile(name = file.name, path = cleanPath, fullPath = file.path)
                else ->
                    FolderItem.Unknown(name = file.name, path = cleanPath, fullPath = file.path)
            }
            folderItems.add(item)
        }
    }
    return folderItems
}

fun Context.getFileType(file: File): String? = if (file.exists()) {
    val fileUri: Uri = Uri.fromFile(file)
    val mimeType = if (ContentResolver.SCHEME_CONTENT == fileUri.scheme) {
        contentResolver.getType(fileUri)
    } else {
        MimeTypeMap.getSingleton().getMimeTypeFromExtension(file.extension.lowercase())
    }
    mimeType
} else {
    null
}

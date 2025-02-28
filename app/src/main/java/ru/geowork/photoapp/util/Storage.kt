package ru.geowork.photoapp.util

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import ru.geowork.photoapp.model.FolderItem
import java.io.File
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.channels.Channels

private const val APP_FOLDER_NAME = "GWApp"

private val APP_FILES_DIRECTORY =
    "${Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).path}/$APP_FOLDER_NAME"

private const val MIME_TYPE_FOLDER = "vnd.android.document/directory"

fun Context.saveFileToDocuments(file: File, path: String) {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, file.name)
        put(MediaStore.MediaColumns.MIME_TYPE, "application/octet-stream")
        put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOCUMENTS}/$APP_FOLDER_NAME/$path/${file.name}")
    }

    val uri = contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
    uri?.let {
        contentResolver.openFileDescriptor(it, "w")?.use { parcelFileDescriptor ->
            FileOutputStream(parcelFileDescriptor.fileDescriptor).use { outputStream ->
                val fileChannel = outputStream.channel
                val inputChannel = Channels.newChannel(file.inputStream())
                val buffer = ByteBuffer.allocateDirect(DEFAULT_BUFFER_SIZE)
                while (inputChannel.read(buffer) > 0) {
                    buffer.flip()
                    fileChannel.write(buffer)
                    buffer.clear()
                }
            }
        }
    }
}

fun Context.createPathInDocuments(folderName: String, path: String) {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, folderName)
        put(MediaStore.MediaColumns.MIME_TYPE, MIME_TYPE_FOLDER)
        put(MediaStore.MediaColumns.RELATIVE_PATH, "${Environment.DIRECTORY_DOCUMENTS}/$APP_FOLDER_NAME/$path/$folderName")
    }
    contentResolver.insert(MediaStore.Files.getContentUri("external"), contentValues)
}

fun Context.getFilesFromDocuments(path: String): List<FolderItem> {
    val folderItems = mutableListOf<FolderItem>()
    val fullPath = File("$APP_FILES_DIRECTORY/$path")
    if (fullPath.exists()) {
        fullPath.listFiles()?.forEach { file ->
            val type = getFileType(file)
            val item = when(type) {
                null -> FolderItem.Folder(name = file.name, path = file.path)
                else -> FolderItem.Unknown(name = file.name, path = file.path)
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

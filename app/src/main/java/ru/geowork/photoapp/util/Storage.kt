package ru.geowork.photoapp.util

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.core.database.getStringOrNull
import ru.geowork.photoapp.BuildConfig

private const val MIME_TYPE_FOLDER = "vnd.android.document/directory"
private const val MIME_TYPE_IMAGE_JPEG = "image/jpeg"
private const val MIME_TYPE_IMAGE_PNG = "image/png"
private const val MIME_TYPE_IMAGE_WEBP = "image/webp"

private val DOCUMENTS_APP_FOLDER = "${Environment.DIRECTORY_DOCUMENTS}/${BuildConfig.APP_FOLDER_NAME}"

fun Context.createFileLikeExt(fileName: String, path: String, ext: String): Uri? {
    val mimeType = getMimeTypeFromExtension(ext) ?: MIME_TYPE_FOLDER
    return createFile(fileName, path, mimeType)
}

fun Context.createFileLikeUri(sourceUri: Uri, fileName: String, path: String): Uri? {
    val mimeType = getMimeTypeFromUri(sourceUri) ?: return null
    return createFile(fileName, path, mimeType)
}

fun Context.createFile(fileName: String, path: String, mimeType: String): Uri? {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
        put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        put(MediaStore.MediaColumns.RELATIVE_PATH, "$DOCUMENTS_APP_FOLDER/$path/")
    }
    val uri = when(mimeType) {
        MIME_TYPE_IMAGE_JPEG, MIME_TYPE_IMAGE_PNG, MIME_TYPE_IMAGE_WEBP -> MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        else -> MediaStore.Files.getContentUri("external")
    }
    return contentResolver.insert(uri, contentValues)
}

fun Context.openOutputStream(uri: Uri) = contentResolver.openOutputStream(uri)

fun Context.openInputStream(uri: Uri) = contentResolver.openInputStream(uri)

fun Context.getFiles(path: String): List<MediaStoreFile> {
    val files = mutableListOf<MediaStoreFile>()

    val collection = MediaStore.Files.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
    val projection = arrayOf(
        MediaStore.Files.FileColumns._ID,
        MediaStore.Files.FileColumns.DISPLAY_NAME,
        MediaStore.Files.FileColumns.MIME_TYPE,
        MediaStore.Files.FileColumns.SIZE
    )
    val selection = "${MediaStore.Files.FileColumns.RELATIVE_PATH} = ?"
    val selectionArgs = arrayOf("$DOCUMENTS_APP_FOLDER/$path/")

    contentResolver.query(collection, projection, selection, selectionArgs, null)?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns._ID)
        val displayNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.DISPLAY_NAME)
        val mimeTypeColumn = cursor.getColumnIndexOrThrow(MediaStore.Files.FileColumns.MIME_TYPE)
        val sizeColumn = cursor.getColumnIndex(MediaStore.Files.FileColumns.SIZE)

        while (cursor.moveToNext()) {
            val id = cursor.getLong(idColumn)
            val displayName = cursor.getStringOrNull(displayNameColumn).orEmpty()
            val mimeType = cursor.getStringOrNull(mimeTypeColumn)
            val size = cursor.getLong(sizeColumn)
            val uri = ContentUris.withAppendedId(collection, id)

            val mediaStoreFile = MediaStoreFile(
                id = id,
                displayName = displayName,
                mimeType = mimeType,
                uri = uri,
                size = size
            )
            files.add(mediaStoreFile)
        }
    }
    return files
}

fun Context.deleteFile(uri: Uri) {
    contentResolver.delete(uri, null, null)
}

fun Context.getMimeTypeFromUri(uri: Uri) = contentResolver.getType(uri)

fun getExtensionFromMimeType(mimeType: String?) = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)

fun getMimeTypeFromExtension(ext: String) = MimeTypeMap.getSingleton().getMimeTypeFromExtension(ext)

data class MediaStoreFile(
    val id: Long,
    val displayName: String,
    val mimeType: String?,
    val uri: Uri,
    val size: Long
)

package ru.geowork.photoapp.util

import android.content.Context
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import id.zelory.compressor.loadBitmap
import id.zelory.compressor.overWrite
import java.io.File
import java.io.FileNotFoundException
import java.io.IOException

fun Context.compressImage(uri: Uri, maxSize: Long): Boolean {
    val mediaStoreFile = getMediaStoreFiles(uri).firstOrNull() ?: throw FileNotFoundException()
    val imageFile = File(mediaStoreFile.fullPath)

    val exif = ExifInterface(imageFile)
    val orientation = exif.getAttribute(ExifInterface.TAG_ORIENTATION)
    exif.setAttribute(ExifInterface.TAG_ORIENTATION, orientation)
    exif.saveAttributes()

    return if (imageFile.length() > maxSize) {
        val tempFile = compressIterative(imageFile, maxSize)

        deleteFile(uri)
        val newUri = createFile(mediaStoreFile.displayName, mediaStoreFile.relativePath, mediaStoreFile.mimeType ?: "") ?: throw IOException()
        tempFile.inputStream().use { inputStream ->
            openOutputStream(newUri)?.use { outputStream ->
                inputStream.copyTo(outputStream)
            }
        }
        tempFile.delete()
        true
    } else {
        false
    }
}

private fun Context.compressIterative(
    imageFile: File,
    maxFileSize: Long,
    stepSize: Int = 2,
    minQuality: Int = 20
): File {
    val bitmap = loadBitmap(imageFile)
    var tempFile = copyToCache(imageFile)
    var iteration = 0
    var quality = 100
    while (tempFile.length() > maxFileSize && quality > minQuality) {
        iteration++
        quality = 100 - iteration * stepSize
        tempFile = overWrite(tempFile, bitmap, quality = quality)
    }
    return tempFile
}

fun Context.copyToCache(imageFile: File): File {
    return imageFile.copyTo(File("${cachePath()}/${imageFile.name}"), true)
}

private fun Context.cachePath() = "${cacheDir.path}/compressor"

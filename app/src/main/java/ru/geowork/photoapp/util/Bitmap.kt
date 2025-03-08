package ru.geowork.photoapp.util

import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.exifinterface.media.ExifInterface

fun Bitmap.rotateIfRequired(exif: ExifInterface): Bitmap {
    val orientation: Int = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL)
    return when (orientation) {
        ExifInterface.ORIENTATION_ROTATE_90 -> rotate(90f)
        ExifInterface.ORIENTATION_ROTATE_180 -> rotate(180f)
        ExifInterface.ORIENTATION_ROTATE_270 -> rotate(270f)
        else -> this
    }
}

fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix().apply { postRotate(degrees) }
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

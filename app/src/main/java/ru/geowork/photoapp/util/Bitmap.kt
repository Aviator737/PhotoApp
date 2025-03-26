package ru.geowork.photoapp.util

import android.graphics.*
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

suspend fun Bitmap.compressToSize(
    targetSize: Long,
    step: Int = 2,
    dispatcher: CoroutineDispatcher
): ByteArray = withContext(dispatcher) {
    ByteArrayOutputStream().use { outputStream ->
        var quality = 100
        do {
            outputStream.reset()
            compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
            quality -= step
        } while (outputStream.toByteArray().size > targetSize && quality > 0)
        outputStream.toByteArray()
    }
}

suspend fun Bitmap.rotate(
    rotationDegrees: Int,
    dispatcher: CoroutineDispatcher
): ByteArray = withContext(dispatcher) {
    ByteArrayOutputStream().use { outputStream ->
        val matrix = Matrix().apply { postRotate(rotationDegrees.toFloat()) }
        val rotatedBitmap = Bitmap.createBitmap(this@rotate, 0, 0, width, height, matrix, true)
        rotatedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        outputStream.toByteArray()
    }
}

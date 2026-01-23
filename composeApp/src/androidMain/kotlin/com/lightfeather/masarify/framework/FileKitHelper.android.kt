package com.lightfeather.masarify.framework

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayOutputStream

/**
 * Android implementation of image compression using Bitmap APIs
 * Uses iterative quality reduction until size is acceptable
 */
@Suppress("ReturnCount")
internal actual suspend fun compressImagePlatform(
    bytes: ByteArray,
    maxSizeBytes: Int,
    maxWidth: Int,
    maxHeight: Int,
): ByteArray {
    // If already under max size, return as-is
    if (bytes.size <= maxSizeBytes) {
        return bytes
    }

    // Decode the image
    val options = BitmapFactory.Options()
    var bitmap =
        BitmapFactory.decodeByteArray(bytes, 0, bytes.size, options)
            ?: return bytes // Return original if decode fails

    // Resize if needed
    val width = bitmap.width
    val height = bitmap.height
    if (width > maxWidth || height > maxHeight) {
        val scale = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()
        bitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }

    // Compress with iterative quality reduction
    var quality = 80
    var compressedBytes: ByteArray

    do {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream)
        compressedBytes = outputStream.toByteArray()
        quality -= 10
    } while (compressedBytes.size > maxSizeBytes && quality > 20)

    bitmap.recycle()
    return compressedBytes
}

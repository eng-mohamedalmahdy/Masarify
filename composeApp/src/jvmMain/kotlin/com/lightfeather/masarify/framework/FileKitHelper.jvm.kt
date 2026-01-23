package com.lightfeather.masarify.framework

import java.awt.Image
import java.awt.image.BufferedImage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import javax.imageio.IIOImage
import javax.imageio.ImageIO
import javax.imageio.ImageWriteParam

/**
 * JVM implementation of image compression using ImageIO APIs
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
    val inputStream = ByteArrayInputStream(bytes)
    var image = ImageIO.read(inputStream) ?: return bytes // Return original if decode fails

    // Resize if needed
    val width = image.width
    val height = image.height
    if (width > maxWidth || height > maxHeight) {
        val scale = minOf(maxWidth.toFloat() / width, maxHeight.toFloat() / height)
        val newWidth = (width * scale).toInt()
        val newHeight = (height * scale).toInt()

        val scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH)
        val bufferedImage = BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB)
        val graphics = bufferedImage.createGraphics()
        graphics.drawImage(scaledImage, 0, 0, null)
        graphics.dispose()
        image = bufferedImage
    }

    // Get JPEG writer
    val writers = ImageIO.getImageWritersByFormatName("jpeg")
    if (!writers.hasNext()) {
        return bytes // Return original if no JPEG writer available
    }
    val writer = writers.next()

    // Compress with iterative quality reduction
    var quality = 0.8f
    var compressedBytes: ByteArray

    do {
        val outputStream = ByteArrayOutputStream()
        val imageOutputStream = ImageIO.createImageOutputStream(outputStream)
        writer.output = imageOutputStream

        val params = writer.defaultWriteParam
        params.compressionMode = ImageWriteParam.MODE_EXPLICIT
        params.compressionQuality = quality

        writer.write(null, IIOImage(image, null, null), params)
        imageOutputStream.close()

        compressedBytes = outputStream.toByteArray()
        quality -= 0.1f
    } while (compressedBytes.size > maxSizeBytes && quality > 0.2f)

    writer.dispose()
    return compressedBytes
}

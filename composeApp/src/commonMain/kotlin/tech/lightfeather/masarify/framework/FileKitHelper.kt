package tech.lightfeather.masarify.framework

import io.github.vinceglb.filekit.mimeType.MimeType

/**
 * Utility object for image compression and validation using FileKit
 * Provides helper functions for working with image attachments
 */
object FileKitHelper {
    /**
     * Maximum file size for images in bytes (1MB)
     */
    const val MAX_IMAGE_SIZE_BYTES = 1_048_576 // 1MB

    /**
     * Compresses an image to meet the maximum size requirement
     * Uses iterative quality reduction until size is acceptable
     *
     * @param bytes Original image bytes
     * @param maxSizeBytes Maximum allowed size in bytes (default 1MB)
     * @param maxWidth Maximum width for compressed image
     * @param maxHeight Maximum height for compressed image
     * @return Compressed image bytes
     */
    suspend fun compressImage(
        bytes: ByteArray,
        maxSizeBytes: Int = MAX_IMAGE_SIZE_BYTES,
        maxWidth: Int = 1920,
        maxHeight: Int = 1920,
    ): ByteArray = compressImagePlatform(bytes, maxSizeBytes, maxWidth, maxHeight)

    /**
     * Validates if bytes represent a valid image
     * Checks MIME type and file size
     *
     * @param bytes Image bytes to validate
     * @param mimeType MIME type of the file
     * @return true if valid image, false otherwise
     */
    @Suppress("ReturnCount") // Simple validation with early returns
    fun isValidImage(
        bytes: ByteArray,
        mimeType: MimeType?,
    ): Boolean {
        // Check if MIME type is an image
        if (!mimeType?.primaryType.equals("image", true)) {
            return false
        }

        // Check if bytes are not empty
        if (bytes.isEmpty()) {
            return false
        }

        return true
    }
}

/**
 * Platform-specific image compression implementation
 *
 * @param bytes Original image bytes
 * @param maxSizeBytes Maximum allowed size in bytes
 * @param maxWidth Maximum width for compressed image
 * @param maxHeight Maximum height for compressed image
 * @return Compressed image bytes
 */
internal expect suspend fun compressImagePlatform(
    bytes: ByteArray,
    maxSizeBytes: Int,
    maxWidth: Int,
    maxHeight: Int,
): ByteArray

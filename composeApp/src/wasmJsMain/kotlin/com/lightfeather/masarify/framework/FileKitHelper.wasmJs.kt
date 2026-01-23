package com.lightfeather.masarify.framework

/**
 * WASM implementation of image compression
 * Note: Web browsers handle image optimization natively, so we keep the original bytes
 * Compression is handled client-side by the browser when rendering
 */
internal actual suspend fun compressImagePlatform(
    bytes: ByteArray,
    maxSizeBytes: Int,
    maxWidth: Int,
    maxHeight: Int,
): ByteArray {
    // For web, return as-is since browsers handle image optimization
    // If size validation is needed, it's done by the caller
    return bytes
}

package com.lightfeather.masarify.framework

import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.compressImage
import kotlinx.cinterop.ExperimentalForeignApi

/**
 * iOS implementation of image compression using UIImage APIs
 * Uses iterative quality reduction until size is acceptable
 */
@OptIn(ExperimentalForeignApi::class)
internal actual suspend fun compressImagePlatform(
    bytes: ByteArray,
    maxSizeBytes: Int,
    maxWidth: Int,
    maxHeight: Int,
): ByteArray =
    FileKit.compressImage(
        bytes = bytes,
        maxWidth = maxWidth,
        maxHeight = maxHeight,
    )

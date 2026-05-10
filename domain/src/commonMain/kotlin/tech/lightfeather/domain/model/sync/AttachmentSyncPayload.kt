package tech.lightfeather.domain.model.sync

import kotlinx.serialization.Serializable

@Serializable
data class AttachmentSyncPayload(
    val remoteId: Long? = null,
    val entityType: String,
    val entityRemoteId: Long,
    val mimeType: String,
    val fileName: String,
    val sizeBytes: Long,
    val data: String,
    val createdAt: Long,
    val deleted: Boolean = false,
)

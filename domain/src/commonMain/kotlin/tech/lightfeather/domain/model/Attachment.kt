package tech.lightfeather.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Attachment(
    val id: Int,
    val remoteId: Long? = null,
    val entityType: AttachmentEntityType,
    val entityId: Int?,
    val mimeType: String,
    val fileName: String,
    val fileContent: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as Attachment

        if (id != other.id) return false
        if (remoteId != other.remoteId) return false
        if (entityType != other.entityType) return false
        if (entityId != other.entityId) return false
        if (mimeType != other.mimeType) return false
        if (fileName != other.fileName) return false
        if (!fileContent.contentEquals(other.fileContent)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id
        result = 31 * result + (remoteId?.hashCode() ?: 0)
        result = 31 * result + entityType.hashCode()
        result = 31 * result + (entityId ?: 0)
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + fileName.hashCode()
        result = 31 * result + fileContent.contentHashCode()
        return result
    }
}

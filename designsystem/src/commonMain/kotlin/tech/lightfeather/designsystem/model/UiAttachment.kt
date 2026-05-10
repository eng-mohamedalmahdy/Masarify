package tech.lightfeather.designsystem.model

data class UiAttachment(
    val id: String,
    val name: String,
    val mimeType: String,
    val fileContent: ByteArray,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || this::class != other::class) return false

        other as UiAttachment

        if (id != other.id) return false
        if (name != other.name) return false
        if (mimeType != other.mimeType) return false
        if (!fileContent.contentEquals(other.fileContent)) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + mimeType.hashCode()
        result = 31 * result + fileContent.contentHashCode()
        return result
    }
}

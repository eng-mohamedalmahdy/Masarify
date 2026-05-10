package tech.lightfeather.domain.usecase

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import tech.lightfeather.domain.model.Attachment
import tech.lightfeather.domain.model.sync.AttachmentSyncPayload
import tech.lightfeather.domain.repository.SyncQueueRepository
import tech.lightfeather.domain.repository.UserRepository
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlin.time.Clock

class SyncEnqueueHelper(
    private val syncQueueRepository: SyncQueueRepository,
    private val userRepository: UserRepository,
    private val drainOutboxQueueUseCase: DrainOutboxQueueUseCase,
    private val pullRemoteDeltaUseCase: PullRemoteDeltaUseCase,
    private val uploadLocalDataUseCase: UploadLocalDataUseCase,
) {
    private val json = Json { ignoreUnknownKeys = true }

    fun isLoggedIn(): Boolean = userRepository.isLoggedIn()

    suspend fun <T> enqueue(
        entityType: String,
        operation: String,
        payload: T,
        serializer: KSerializer<T>,
        localId: Int,
    ) {
        if (!userRepository.isLoggedIn()) return
        if (syncQueueRepository.hasActiveEntry(localId.toLong(), entityType)) return
        syncQueueRepository.insertEntry(
            entityType = entityType,
            operation = operation,
            payload = json.encodeToString(serializer, payload),
            localId = localId.toLong(),
            remoteId = null,
        )
        drainOutboxQueueUseCase()
    }

    suspend fun fullSync() {
        if (!userRepository.isLoggedIn()) return
        // Pass 1: pull any existing remoteIds, then enqueue+send the first dependency layer
        // (currencies, categories). After drain, the server has assigned remoteIds for them.
        pullRemoteDeltaUseCase()
        uploadLocalDataUseCase()
        drainOutboxQueueUseCase()
        // Pass 2: pull the newly assigned remoteIds so that dependent entities (accounts,
        // transactions, sessions) can now resolve their dependency remoteIds during upload.
        pullRemoteDeltaUseCase()
        uploadLocalDataUseCase()
        drainOutboxQueueUseCase()
    }

    @OptIn(ExperimentalEncodingApi::class)
    suspend fun enqueueAttachmentCreate(localId: Int, attachment: Attachment, entityRemoteId: Long) {
        if (!userRepository.isLoggedIn()) return
        val base64Data = Base64.encode(attachment.fileContent)
        val payload = AttachmentSyncPayload(
            entityType = attachment.entityType.name.uppercase(),
            entityRemoteId = entityRemoteId,
            mimeType = attachment.mimeType,
            fileName = attachment.fileName,
            sizeBytes = attachment.fileContent.size.toLong(),
            data = base64Data,
            createdAt = Clock.System.now().toEpochMilliseconds(),
        )
        syncQueueRepository.insertEntry(
            entityType = "ATTACHMENT",
            operation = "CREATE",
            payload = json.encodeToString(AttachmentSyncPayload.serializer(), payload),
            localId = localId.toLong(),
            remoteId = null,
        )
        drainOutboxQueueUseCase()
    }
}

package tech.lightfeather.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map
import tech.lightfeather.data.local.database.drivers.SharedDatabase
import tech.lightfeather.data.util.IoDispatcher
import tech.lightfeather.domain.model.Attachment
import tech.lightfeather.domain.model.AttachmentEntityType
import tech.lightfeather.domain.model.DomainResult
import tech.lightfeather.domain.model.runCatchingDomainResultSuspend
import tech.lightfeather.domain.repository.AttachmentRepository

/**
 * Implementation of AttachmentRepository using SQLDelight
 * Manages attachment CRUD operations in the database
 */
class AttachmentRepositoryImpl(
    private val sharedDatabase: SharedDatabase,
) : AttachmentRepository {
    override suspend fun createAttachment(attachment: Attachment): DomainResult<Int> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                val queries = it.attachmentsQueries
                queries.insertAttachment(
                    entity_type = attachment.entityType.value,
                    entity_id = attachment.entityId?.toLong(),
                    name = attachment.fileName,
                    data = attachment.fileContent,
                    mime_type = attachment.mimeType,
                )
                queries.selectLastInsertedAttachmentId().awaitAsOne().toInt()
            }
        }

    override suspend fun updateRemoteId(localId: Int, remoteId: Long): DomainResult<Unit> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                it.attachmentsQueries.updateAttachmentRemoteId(remote_id = remoteId, id = localId.toLong())
            }
        }

    override suspend fun getUnsyncedIds(): DomainResult<List<Int>> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                it.attachmentsQueries.getUnsyncedAttachments().awaitAsList().map { row -> row.id.toInt() }
            }
        }

    override suspend fun getByRemoteId(remoteId: Long): DomainResult<Attachment?> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                it.attachmentsQueries.getAttachmentByRemoteId(remote_id = remoteId).awaitAsList().firstOrNull()
                    ?.let { row ->
                        Attachment(
                            id = row.id.toInt(),
                            remoteId = row.remote_id,
                            entityType = AttachmentEntityType.fromValue(row.entity_type)
                                ?: AttachmentEntityType.TRANSACTION,
                            entityId = row.entity_id?.toInt(),
                            fileName = row.name.orEmpty(),
                            mimeType = row.mime_type,
                            fileContent = row.data_,
                        )
                    }
            }
        }

    override suspend fun getAttachmentsOfEntityType(type: AttachmentEntityType): DomainResult<Flow<List<Attachment>>> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                val queries = it.attachmentsQueries
                queries
                    .getAttachmentsByEntity(type.name.lowercase(), null)
                    .asFlow()
                    .mapToList(Dispatchers.IoDispatcher)
                    .map {
                        it.map { row ->
                            Attachment(
                                id = row.attachmentId.toInt(),
                                remoteId = row.remoteId,
                                entityType =
                                    AttachmentEntityType.fromValue(row.entityType.orEmpty())
                                        ?: AttachmentEntityType.CATEGORY,
                                entityId = row.entityId?.toInt(),
                                fileName = row.attachmentName.orEmpty(),
                                mimeType = row.attachmentMimeType,
                                fileContent = row.attachmentData,
                            )
                        }
                    }
            }
        }

    override suspend fun deleteAttachment(id: Int): DomainResult<Boolean> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                it.attachmentsQueries.deleteAttachmentById(id.toLong())
            }
            true
        }

    override suspend fun getAttachmentsByEntity(
        entityType: AttachmentEntityType,
        entityId: Int,
    ): DomainResult<List<Attachment>> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                it.attachmentsQueries
                    .getAttachmentsByEntity(
                        entity_type = entityType.value,
                        entity_id = entityId.toLong(),
                    ).awaitAsList()
                    .map { row ->
                        Attachment(
                            id = row.attachmentId.toInt(),
                            remoteId = row.remoteId,
                            entityType =
                                AttachmentEntityType.fromValue(row.entityType.orEmpty())
                                    ?: AttachmentEntityType.TRANSACTION,
                            entityId = row.entityId?.toInt(),
                            fileName = row.attachmentName.orEmpty(),
                            mimeType = row.attachmentMimeType,
                            fileContent = row.attachmentData,
                        )
                    }
            }
        }

    override suspend fun deleteAttachmentsByEntity(
        entityType: AttachmentEntityType,
        entityId: Int,
    ): DomainResult<Boolean> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                it.attachmentsQueries.deleteAttachmentsByEntity(
                    entity_type = entityType.value,
                    entity_id = entityId.toLong(),
                )
            }
            true
        }
}

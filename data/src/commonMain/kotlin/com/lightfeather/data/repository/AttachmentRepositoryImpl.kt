package com.lightfeather.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import com.lightfeather.data.local.database.drivers.SharedDatabase
import com.lightfeather.data.util.IoDispatcher
import com.lightfeather.domain.model.Attachment
import com.lightfeather.domain.model.AttachmentEntityType
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.runCatchingDomainResultSuspend
import com.lightfeather.domain.repository.AttachmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map

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

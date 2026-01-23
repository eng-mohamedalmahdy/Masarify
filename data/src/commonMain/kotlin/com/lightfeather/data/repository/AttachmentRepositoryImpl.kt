package com.lightfeather.data.repository

import app.cash.sqldelight.async.coroutines.awaitAsList
import app.cash.sqldelight.async.coroutines.awaitAsOne
import com.lightfeather.data.local.database.drivers.SharedDatabase
import com.lightfeather.domain.model.Attachment
import com.lightfeather.domain.model.DomainResult
import com.lightfeather.domain.model.runCatchingDomainResultSuspend
import com.lightfeather.domain.repository.AttachmentRepository

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
                    transaction_id = attachment.transactionId.toLong(),
                    name = attachment.fileName,
                    data = attachment.fileContent,
                    mime_type = attachment.mimeType,
                )
                queries.selectLastInsertedAttachmentId().awaitAsOne().toInt()
            }
        }

    override suspend fun deleteAttachment(id: Int): DomainResult<Boolean> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                it.attachmentsQueries.deleteAttachmentById(id.toLong())
            }
            true
        }

    override suspend fun getAttachmentsByTransactionId(transactionId: Int): DomainResult<List<Attachment>> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                it.attachmentsQueries
                    .getAttachmentsByTransactionId(transactionId.toLong())
                    .awaitAsList()
                    .map { row ->
                        Attachment(
                            id = row.attachmentId.toInt(),
                            transactionId = row.transactionId.toInt(),
                            fileName = row.attachmentName.orEmpty(),
                            mimeType = row.attachmentMimeType.orEmpty(),
                            fileContent = row.attachmentData ?: byteArrayOf(),
                        )
                    }
            }
        }

    override suspend fun deleteAttachmentsByTransactionId(transactionId: Int): DomainResult<Boolean> =
        runCatchingDomainResultSuspend {
            sharedDatabase {
                it.attachmentsQueries.deleteAttachmentsByTransactionId(transactionId.toLong())
            }
            true
        }
}

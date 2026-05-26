package tech.lightfeather.masarify.di

import org.koin.dsl.module
import tech.lightfeather.data.repository.AttachmentRepositoryImpl
import tech.lightfeather.domain.repository.AttachmentRepository

/**
 * Koin module for attachment-related dependencies
 */
val attachmentModule =
    module {
        single<AttachmentRepository> {
            AttachmentRepositoryImpl(
                sharedDatabase = get(),
            )
        }
    }

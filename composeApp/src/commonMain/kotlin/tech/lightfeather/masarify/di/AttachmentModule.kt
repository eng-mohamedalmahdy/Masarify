package tech.lightfeather.masarify.di

import tech.lightfeather.data.repository.AttachmentRepositoryImpl
import tech.lightfeather.domain.repository.AttachmentRepository
import org.koin.dsl.module

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

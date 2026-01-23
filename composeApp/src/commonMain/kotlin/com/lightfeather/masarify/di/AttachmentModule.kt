package com.lightfeather.masarify.di

import com.lightfeather.data.repository.AttachmentRepositoryImpl
import com.lightfeather.domain.repository.AttachmentRepository
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

package tech.lightfeather.masarify.test

import tech.lightfeather.designsystem.model.UiCategory
import tech.lightfeather.domain.usecase.CreateCategory
import tech.lightfeather.domain.usecase.GetAllCategoryIcons
import tech.lightfeather.domain.usecase.GetUserSavedColors
import tech.lightfeather.domain.usecase.SaveUserColor
import tech.lightfeather.domain.usecase.UpdateCategory
import tech.lightfeather.masarify.page.categories.addedit.AddEditCategoryPageViewModel

internal fun buildAddEditCategoryViewModel(
    initialCategory: UiCategory? = null,
    onBackCallback: () -> Unit = {},
): AddEditCategoryPageViewModel {
    val categoryRepo = FakeCategoryRepository()
    val attachmentRepo = FakeAttachmentRepository()
    val userRepo = FakeUserRepository()
    val syncHelper = buildSyncEnqueueHelper()
    return AddEditCategoryPageViewModel(
        createCategory = CreateCategory(categoryRepo, syncHelper),
        updateCategory = UpdateCategory(categoryRepo, syncHelper),
        getAllCategoryIcons = GetAllCategoryIcons(categoryRepo, attachmentRepo),
        getUserSavedColors = GetUserSavedColors(userRepo),
        saveUserColor = SaveUserColor(userRepo),
        attachmentsRepository = attachmentRepo,
        onBackCallback = onBackCallback,
        initialCategory = initialCategory,
    )
}

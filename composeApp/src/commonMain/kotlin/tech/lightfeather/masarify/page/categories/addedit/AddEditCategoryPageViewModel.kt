package tech.lightfeather.masarify.page.categories.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import tech.lightfeather.data.util.IoDispatcher
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import tech.lightfeather.designsystem.model.UiCategory
import tech.lightfeather.domain.model.Attachment
import tech.lightfeather.domain.model.AttachmentEntityType
import tech.lightfeather.domain.model.Category
import tech.lightfeather.domain.repository.AttachmentRepository
import tech.lightfeather.domain.usecase.CreateCategory
import tech.lightfeather.domain.usecase.GetAllCategoryIcons
import tech.lightfeather.domain.usecase.GetUserSavedColors
import tech.lightfeather.domain.usecase.SaveUserColor
import tech.lightfeather.domain.usecase.UpdateCategory
import io.github.aakira.napier.Napier
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.mimeType
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddEditCategoryPageViewModel(
    private val createCategory: CreateCategory,
    private val updateCategory: UpdateCategory,
    private val getAllCategoryIcons: GetAllCategoryIcons,
    private val getUserSavedColors: GetUserSavedColors,
    private val saveUserColor: SaveUserColor,
    private val attachmentsRepository: AttachmentRepository,
    private val onBackCallback: () -> Unit,
    initialCategory: UiCategory?,
) : ViewModel() {
    private val _state =
        MutableStateFlow(
            initialCategory?.takeIf { it != UiCategory.empty }?.let { AddEditCategoryPageState.fromCategory(it) }
                ?: AddEditCategoryPageState().also { Napier.d { "WHAT VM SEES IS $it" } },
        )
    internal val state: StateFlow<AddEditCategoryPageState> = _state

    init {
        onIntent(AddEditCategoryPageIntent.LoadData)
    }

    @Suppress("CyclomaticComplexMethod") // Business logic: handles multiple intent types
    internal fun onIntent(intent: AddEditCategoryPageIntent) {
        when (intent) {
            is AddEditCategoryPageIntent.LoadData -> {
                loadRecentColors()
                loadIcons()
            }

            is AddEditCategoryPageIntent.UpdateName -> {
                _state.value = _state.value.copy(name = intent.name)
            }

            is AddEditCategoryPageIntent.UpdateDescription -> {
                _state.value = _state.value.copy(description = intent.description)
            }

            is AddEditCategoryPageIntent.UpdateCustomIconUrl -> {
                _state.value = _state.value.copy(customIconUrl = intent.url)
            }

            is AddEditCategoryPageIntent.SelectColor -> {
                val currentRecentColors = _state.value.recentColors.toMutableList()
                if (!currentRecentColors.contains(intent.color)) {
                    currentRecentColors.add(0, intent.color)
                    if (currentRecentColors.size > 5) {
                        currentRecentColors.removeLast()
                    }
                }
                loadRecentColors()
                _state.value = _state.value.copy(selectedColor = intent.color)
            }

            is AddEditCategoryPageIntent.SelectIcon -> {
                _state.value =
                    _state.value.copy(
                        selectedIcon = intent.icon,
                        customIconUrl = "",
                    )
            }

            is AddEditCategoryPageIntent.ToggleColorPicker -> {
                _state.value = _state.value.copy(showColorPicker = intent.show)
            }

            is AddEditCategoryPageIntent.Save -> {
                saveCategory()
            }

            is AddEditCategoryPageIntent.Cancel -> {
                onBackCallback()
                _state.value = AddEditCategoryPageState()
            }

            is AddEditCategoryPageIntent.SaveRecentColor -> {
                saveUserColor(intent.color)
            }

            AddEditCategoryPageIntent.AddIconFromGallery -> {
                viewModelScope.launch(Dispatchers.IoDispatcher) {
                    val pickerResult =
                        FileKit.openFilePicker(
                            type = FileKitType.Image,
                            mode = FileKitMode.Single,
                        )
                    pickerResult?.let {
                        attachmentsRepository
                            .createAttachment(
                                Attachment(
                                    id = 0,
                                    entityType = AttachmentEntityType.CATEGORY,
                                    entityId = null,
                                    mimeType = pickerResult.mimeType()?.primaryType.orEmpty(),
                                    fileName = pickerResult.name,
                                    fileContent = pickerResult.readBytes(),
                                ),
                            ).fold(
                                onSuccess = {
                                    Napier.d { "$it" }
                                    SnackbarService.sendSuccessMessage(MR.strings.category_updated_success)
                                },
                                onFailure = {
                                    Napier.d { "$it" }
                                },
                            )
                    }
                }
            }
        }
    }

    private fun loadIcons() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoadingIcons = true)

            getAllCategoryIcons().foldResult(
                onSuccess = { iconsFlow ->
                    iconsFlow.collect { icons ->
                        _state.value =
                            _state.value.copy(
                                availableIcons = icons,
                                isLoadingIcons = false,
                                selectedIcon = _state.value.selectedIcon ?: icons.firstOrNull(),
                            )
                    }
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(isLoadingIcons = false)
                    SnackbarService.sendErrorMessage(MR.strings.failed_to_load_icons)
                },
            )
        }
    }

    private fun loadRecentColors() {
        val colors = getUserSavedColors()
        _state.value = _state.value.copy(recentColors = colors)
    }

    private fun saveCategory() {
        val currentState = _state.value

        // Validation
        if (currentState.name.isBlank()) {
            SnackbarService.sendErrorMessage(MR.strings.category_name_required)
            return
        }

        if (currentState.selectedIcon == null && currentState.customIconUrl.isBlank()) {
            SnackbarService.sendErrorMessage(MR.strings.category_icon_required)
            return
        }

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            val iconValue = currentState.toBeAddedIcon.orEmpty()

            val category =
                Category(
                    id = currentState.categoryId ?: 0,
                    name = currentState.name,
                    description = currentState.description.takeIf { it.isNotBlank() },
                    color = currentState.selectedColor,
                    icon = iconValue,
                )

            val result =
                if (currentState.isEditMode) {
                    updateCategory(category)
                } else {
                    createCategory(category)
                }

            result.foldResult(
                onSuccess = {
                    _state.value = _state.value.copy(isLoading = false)
                    SnackbarService.sendSuccessMessage(
                        if (currentState.isEditMode) {
                            MR.strings.category_updated_success
                        } else {
                            MR.strings.category_created_success
                        },
                    )
                    _state.value = AddEditCategoryPageState()
                    onBackCallback()
                },
                onFailure = { error ->
                    _state.value = _state.value.copy(isLoading = false)
                    SnackbarService.sendErrorMessage(
                        if (currentState.isEditMode) {
                            MR.strings.category_update_failure
                        } else {
                            MR.strings.category_create_failure
                        },
                    )
                },
            )
        }
    }
}

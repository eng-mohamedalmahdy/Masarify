package com.lightfeather.masarify.page.categories.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import com.lightfeather.designsystem.model.UiCategory
import com.lightfeather.domain.model.Category
import com.lightfeather.domain.usecase.CreateCategory
import com.lightfeather.domain.usecase.GetAllCategoryIcons
import com.lightfeather.domain.usecase.GetUserSavedColors
import com.lightfeather.domain.usecase.SaveUserColor
import com.lightfeather.domain.usecase.UpdateCategory
import com.lightfeather.masarify.MR
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddEditCategoryPageViewModel(
    private val createCategory: CreateCategory,
    private val updateCategory: UpdateCategory,
    private val getAllCategoryIcons: GetAllCategoryIcons,
    private val getUserSavedColors: GetUserSavedColors,
    private val saveUserColor: SaveUserColor,
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
            }

            is AddEditCategoryPageIntent.SaveRecentColor -> {
                saveUserColor(intent.color)
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

            val iconValue =
                currentState.customIconUrl.takeIf { it.isNotBlank() }
                    ?: currentState.selectedIcon.toString()

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

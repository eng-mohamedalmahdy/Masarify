package com.lightfeather.masarify.page.more

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.domain.model.AppLanguage
import com.lightfeather.domain.usecase.GetUserDarkMode
import com.lightfeather.domain.usecase.GetUserLanguage
import com.lightfeather.domain.usecase.SetLanguage
import com.lightfeather.domain.usecase.ToggleDarkMode
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MorePageViewModel(
    private val isDarkModeEnabled: GetUserDarkMode,
    private val toggleDarkMode: ToggleDarkMode,
    private val setUserLanguage: SetLanguage,
    private val getLanguage: GetUserLanguage,
) : ViewModel() {
    private val _state = MutableStateFlow(MorePageState())
    internal val state: StateFlow<MorePageState> = _state

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    internal fun onIntent(intent: MorePageIntent) {
        when (intent) {
            is MorePageIntent.LoadData -> {
                loadInitialData()
            }

            is MorePageIntent.ToggleDarkTheme -> {
                viewModelScope.launch {
                    toggleDarkMode()
                    _state.value = _state.value.copy(isDarkTheme = intent.enabled)
                    SnackbarService.sendSuccessMessage(
                        if (intent.enabled) MR.strings.dark_theme_enabled else MR.strings.dark_theme_disabled,
                    )
                }
            }

            is MorePageIntent.SelectLanguage -> {
                viewModelScope.launch {
                    setUserLanguage(intent.language)
                    _state.value = _state.value.copy(selectedLanguage = intent.language)

                    // Update the locale for moko-resources
                    StringDesc.localeType = StringDesc.LocaleType.Custom(intent.language.code)

                    SnackbarService.sendSuccessMessage(MR.strings.language_changed)
                }
            }

            is MorePageIntent.ClearNavigation -> {
                viewModelScope.launch {
                    while (intent.navigator.canNavigateBack()) {
                        intent.navigator.navigateBack()
                    }
                    _state.value = _state.value.copy(selectedDetailItem = null)
                }
            }

            is MorePageIntent.NavigationIntent -> {
                viewModelScope.launch {
                    while (intent.navigator.canNavigateBack()) {
                        intent.navigator.navigateBack()
                    }
                    _state.value = _state.value.copy(selectedDetailItem = null)
                    intent.navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, intent)
                    _state.value = _state.value.copy(selectedDetailItem = intent.detailItem)
                }
            }
        }
    }

    private fun loadInitialData() {
        _state.value = _state.value.copy(isLoading = true)

        viewModelScope.launch {
            // Load current user preferences
            val currentLanguage = getLanguage()
            val darkModeEnabled = isDarkModeEnabled()
            _state.value =
                _state.value.copy(
                    selectedLanguage = currentLanguage,
                    isDarkTheme = darkModeEnabled,
                    isLoading = false,
                )
        }
    }
}

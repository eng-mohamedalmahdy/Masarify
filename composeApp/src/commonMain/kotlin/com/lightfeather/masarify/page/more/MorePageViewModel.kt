package com.lightfeather.masarify.page.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import com.lightfeather.domain.repository.UserRepository
import com.lightfeather.domain.usecase.ExportDataUseCase
import com.lightfeather.domain.usecase.GetUserDarkMode
import com.lightfeather.domain.usecase.GetUserLanguage
import com.lightfeather.domain.usecase.ImportDataUseCase
import com.lightfeather.masarify.framework.saveBackupFile
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock

class MorePageViewModel(
    private val isDarkModeEnabled: GetUserDarkMode,
    private val getLanguage: GetUserLanguage,
    private val userRepository: UserRepository,
    private val exportDataUseCase: ExportDataUseCase,
    private val importDataUseCase: ImportDataUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(MorePageState())
    internal val state: StateFlow<MorePageState> = _state

    @Suppress("CyclomaticComplexMethod")
    internal fun onIntent(intent: MorePageIntent) {
        when (intent) {
            is MorePageIntent.LoadData -> {
                loadInitialData()
            }

            is MorePageIntent.ToggleDarkTheme -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(isDarkTheme = intent.enabled)
                    SnackbarService.sendSuccessMessage(
                        if (intent.enabled) MR.strings.dark_theme_enabled else MR.strings.dark_theme_disabled,
                    )
                }
            }

            is MorePageIntent.ToggleBiometric -> {
                viewModelScope.launch {
                    userRepository.setBiometricEnabled(intent.enabled)
                    _state.value = _state.value.copy(isBiometricEnabled = intent.enabled)
                    SnackbarService.sendSuccessMessage(
                        if (intent.enabled) MR.strings.biometric_enabled else MR.strings.biometric_disabled,
                    )
                }
            }

            is MorePageIntent.ToggleAutoSyncRates -> {
                viewModelScope.launch {
                    userRepository.setAutoSyncRatesEnabled(intent.enabled)
                    _state.value = _state.value.copy(isAutoSyncRatesEnabled = intent.enabled)
                    SnackbarService.sendSuccessMessage(
                        if (intent.enabled) MR.strings.auto_sync_rates_enabled else MR.strings.auto_sync_rates_disabled,
                    )
                }
            }

            is MorePageIntent.SelectLanguage -> {
                viewModelScope.launch {
                    _state.value = _state.value.copy(selectedLanguage = intent.language)
                    StringDesc.localeType = StringDesc.LocaleType.Custom(intent.language.code)

                    SnackbarService.sendSuccessMessage(MR.strings.language_changed)
                }
            }

            is MorePageIntent.ClearNavigation -> {
                _state.value = _state.value.copy(selectedDetailItem = null)
            }

            is MorePageIntent.NavigationIntent.SelectCurrencyManagementDetail -> {
                _state.value = _state.value.copy(selectedDetailItem = MoreDetailItem.CurrencyManagement)
            }

            is MorePageIntent.NavigationIntent.SelectPrivacyPolicyDetail -> {
                _state.value = _state.value.copy(selectedDetailItem = MoreDetailItem.PrivacyPolicy)
            }

            is MorePageIntent.NavigationIntent.SelectContactUsDetail -> {
                _state.value = _state.value.copy(selectedDetailItem = MoreDetailItem.ContactUs)
            }

            is MorePageIntent.NavigationIntent.SelectRateUsDetail -> {
                _state.value = _state.value.copy(selectedDetailItem = MoreDetailItem.RateUs)
            }

            is MorePageIntent.NavigationIntent.SelectCategoryManagementDetail -> {
                _state.value = _state.value.copy(selectedDetailItem = MoreDetailItem.CategoryManagement)
            }

            is MorePageIntent.NavigationIntent.SelectBackupRestoreDetail -> {
                _state.value = _state.value.copy(selectedDetailItem = MoreDetailItem.BackupRestore)
            }

            is MorePageIntent.ExportData -> {
                handleExport()
            }

            is MorePageIntent.ImportData -> {
                handleImport(intent)
            }
        }
    }

    private fun handleExport() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isExporting = true)
            exportDataUseCase().foldSuspend(
                onSuccess = { bytes ->
                    val date =
                        Clock.System
                            .now()
                            .toLocalDateTime(TimeZone.currentSystemDefault())
                    val fileName = "masarify_${date.year}-${
                        date.monthNumber.toString().padStart(2, '0')
                    }-${date.dayOfMonth.toString().padStart(2, '0')}.masarify"
                    val saved = saveBackupFile(bytes, fileName)
                    if (saved) {
                        SnackbarService.sendSuccessMessage(MR.strings.export_success)
                    }
                },
                onFailure = {
                    SnackbarService.sendErrorMessage(MR.strings.export_failure)
                },
            )
            _state.value = _state.value.copy(isExporting = false)
        }
    }

    private fun handleImport(intent: MorePageIntent.ImportData) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isImporting = true)
            importDataUseCase(intent.bytes, intent.mode).fold(
                onSuccess = {
                    SnackbarService.sendSuccessMessage(MR.strings.import_success)
                },
                onFailure = {
                    SnackbarService.sendErrorMessage(MR.strings.import_failure)
                },
            )
            _state.value = _state.value.copy(isImporting = false)
        }
    }

    private fun loadInitialData() {
        _state.value = _state.value.copy(isLoading = true)

        viewModelScope.launch {
            // Load current user preferences
            val currentLanguage = getLanguage()
            val darkModeEnabled = isDarkModeEnabled()
            val biometricEnabled = userRepository.isBiometricEnabled()
            val autoSyncEnabled = userRepository.isAutoSyncRatesEnabled()
            _state.value =
                _state.value.copy(
                    selectedLanguage = currentLanguage,
                    isDarkTheme = darkModeEnabled,
                    isBiometricEnabled = biometricEnabled,
                    isAutoSyncRatesEnabled = autoSyncEnabled,
                    isLoading = false,
                )
        }
    }
}

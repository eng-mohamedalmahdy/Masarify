package tech.lightfeather.masarify.page.more

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.icerock.moko.resources.desc.StringDesc
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import tech.lightfeather.data.util.IoDispatcher
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.snackbar.SnackbarService
import tech.lightfeather.domain.repository.UserRepository
import tech.lightfeather.domain.usecase.DeleteAllFailedSyncUseCase
import tech.lightfeather.domain.usecase.DeleteFailedSyncEntryUseCase
import tech.lightfeather.domain.usecase.DrainOutboxQueueUseCase
import tech.lightfeather.domain.usecase.ExportDataUseCase
import tech.lightfeather.domain.usecase.GetFailedSyncCountUseCase
import tech.lightfeather.domain.usecase.GetFailedSyncEntriesUseCase
import tech.lightfeather.domain.usecase.GetUserDarkMode
import tech.lightfeather.domain.usecase.GetUserLanguage
import tech.lightfeather.domain.usecase.ImportDataUseCase
import tech.lightfeather.domain.usecase.IsAuthenticatedUseCase
import tech.lightfeather.domain.usecase.IsEmailVerifiedUseCase
import tech.lightfeather.domain.usecase.LogoutAllDevicesUseCase
import tech.lightfeather.domain.usecase.LogoutUseCase
import tech.lightfeather.domain.usecase.PullRemoteDeltaUseCase
import tech.lightfeather.domain.usecase.ResendVerificationUseCase
import tech.lightfeather.domain.usecase.RetryAllFailedSyncUseCase
import tech.lightfeather.domain.usecase.RetrySyncEntryUseCase
import tech.lightfeather.domain.usecase.UploadLocalDataUseCase
import tech.lightfeather.masarify.framework.saveBackupFile
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.navigation.routes.LoginRoute
import tech.lightfeather.masarify.navigation.routes.OnBoardingRoute
import kotlin.time.Clock

@Suppress("LongParameterList") // Sync use cases + auth check required alongside existing dependencies
class MorePageViewModel(
    private val isDarkModeEnabled: GetUserDarkMode,
    private val getLanguage: GetUserLanguage,
    private val userRepository: UserRepository,
    private val exportDataUseCase: ExportDataUseCase,
    private val importDataUseCase: ImportDataUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val logoutAllDevicesUseCase: LogoutAllDevicesUseCase,
    private val isEmailVerifiedUseCase: IsEmailVerifiedUseCase,
    private val resendVerificationUseCase: ResendVerificationUseCase,
    private val navigator: Navigator,
    private val isAuthenticatedUseCase: IsAuthenticatedUseCase,
    private val uploadLocalDataUseCase: UploadLocalDataUseCase,
    private val drainOutboxQueueUseCase: DrainOutboxQueueUseCase,
    private val pullRemoteDeltaUseCase: PullRemoteDeltaUseCase,
    private val getFailedSyncCountUseCase: GetFailedSyncCountUseCase,
    private val getFailedSyncEntriesUseCase: GetFailedSyncEntriesUseCase,
    private val retrySyncEntryUseCase: RetrySyncEntryUseCase,
    private val retryAllFailedSyncUseCase: RetryAllFailedSyncUseCase,
    private val deleteFailedSyncEntryUseCase: DeleteFailedSyncEntryUseCase,
    private val deleteAllFailedSyncUseCase: DeleteAllFailedSyncUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(MorePageState())
    internal val state: StateFlow<MorePageState> = _state

    @Suppress("CyclomaticComplexMethod", "LongMethod")
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

            is MorePageIntent.ToggleAutoSyncData -> {
                viewModelScope.launch {
                    userRepository.setAutoSyncDataEnabled(intent.enabled)
                    _state.value = _state.value.copy(isAutoSyncDataEnabled = intent.enabled)
                    SnackbarService.sendSuccessMessage(
                        if (intent.enabled) MR.strings.auto_sync_data_enabled else MR.strings.auto_sync_data_disabled,
                    )
                }
            }

            is MorePageIntent.SyncNow -> {
                viewModelScope.launch(Dispatchers.IoDispatcher) {
                    _state.value = _state.value.copy(isSyncing = true)
                    runCatching {
                        uploadLocalDataUseCase()
                        drainOutboxQueueUseCase()
                        pullRemoteDeltaUseCase()
                        SnackbarService.sendSuccessMessage(MR.strings.sync_success)
                    }.onFailure {
                        SnackbarService.sendErrorMessage(MR.strings.sync_failure)
                    }
                    refreshFailedState()
                    _state.value = _state.value.copy(isSyncing = false)
                }
            }

            is MorePageIntent.NavigateToSignIn -> navigator.navigate(LoginRoute)

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

            is MorePageIntent.NavigationIntent.SelectNotificationSettingsDetail -> {
                _state.value = _state.value.copy(selectedDetailItem = MoreDetailItem.NotificationSettings)
            }

            is MorePageIntent.ExportData -> {
                handleExport()
            }

            is MorePageIntent.ImportData -> {
                handleImport(intent)
            }

            MorePageIntent.Logout -> {
                viewModelScope.launch(Dispatchers.IoDispatcher) {
                    logoutUseCase()
                    SnackbarService.sendSuccessMessage(MR.strings.logout_success)
                    navigator.navigateAndClearBackStack(OnBoardingRoute)
                }
            }

            MorePageIntent.ShowLogoutAllDialog -> {
                _state.value = _state.value.copy(isLogoutAllDialogVisible = true)
            }

            MorePageIntent.DismissLogoutAllDialog -> {
                _state.value = _state.value.copy(isLogoutAllDialogVisible = false)
            }

            MorePageIntent.LogoutAllDevices -> {
                _state.value = _state.value.copy(isLogoutAllDialogVisible = false)
                viewModelScope.launch(Dispatchers.IoDispatcher) {
                    logoutAllDevicesUseCase()
                    SnackbarService.sendSuccessMessage(MR.strings.logout_all_success)
                    navigator.navigateAndClearBackStack(OnBoardingRoute)
                }
            }

            MorePageIntent.ResendVerification -> {
                viewModelScope.launch(Dispatchers.IoDispatcher) {
                    resendVerificationUseCase()
                    SnackbarService.sendSuccessMessage(MR.strings.verify_email_resend_success)
                }
            }

            MorePageIntent.ToggleFailedExpanded -> {
                val expanding = !_state.value.isFailedExpanded
                if (expanding) {
                    viewModelScope.launch(Dispatchers.IoDispatcher) {
                        val entries = getFailedSyncEntriesUseCase()
                        _state.value =
                            _state.value.copy(
                                isFailedExpanded = true,
                                failedEntries = entries,
                            )
                    }
                } else {
                    _state.value = _state.value.copy(isFailedExpanded = false)
                }
            }

            is MorePageIntent.RetrySyncEntry -> {
                viewModelScope.launch(Dispatchers.IoDispatcher) {
                    retrySyncEntryUseCase(intent.id)
                    drainOutboxQueueUseCase()
                    refreshFailedState()
                    SnackbarService.sendSuccessMessage(MR.strings.sync_success)
                }
            }

            MorePageIntent.RetryAllFailed -> {
                viewModelScope.launch(Dispatchers.IoDispatcher) {
                    retryAllFailedSyncUseCase()
                    refreshFailedState()
                    SnackbarService.sendSuccessMessage(MR.strings.sync_success)
                }
            }

            is MorePageIntent.DeleteFailedEntry -> {
                viewModelScope.launch(Dispatchers.IoDispatcher) {
                    deleteFailedSyncEntryUseCase(intent.id)
                    refreshFailedState()
                }
            }

            MorePageIntent.DeleteAllFailed -> {
                viewModelScope.launch(Dispatchers.IoDispatcher) {
                    deleteAllFailedSyncUseCase()
                    refreshFailedState()
                }
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
            val autoSyncRatesEnabled = userRepository.isAutoSyncRatesEnabled()
            val autoSyncDataEnabled = userRepository.isAutoSyncDataEnabled()
            val authenticated = isAuthenticatedUseCase()
            val emailVerified = isEmailVerifiedUseCase()
            val failedCount = getFailedSyncCountUseCase().toInt()
            _state.value =
                _state.value.copy(
                    selectedLanguage = currentLanguage,
                    isDarkTheme = darkModeEnabled,
                    isBiometricEnabled = biometricEnabled,
                    isAutoSyncRatesEnabled = autoSyncRatesEnabled,
                    isAutoSyncDataEnabled = autoSyncDataEnabled,
                    isAuthenticated = authenticated,
                    isEmailVerified = emailVerified,
                    failedSyncCount = failedCount,
                    isLoading = false,
                )
        }
    }

    private suspend fun refreshFailedState() {
        val count = getFailedSyncCountUseCase().toInt()
        val entries = if (_state.value.isFailedExpanded) getFailedSyncEntriesUseCase() else emptyList()
        _state.value =
            _state.value.copy(
                failedSyncCount = count,
                failedEntries = entries,
                isFailedExpanded = if (count == 0) false else _state.value.isFailedExpanded,
            )
    }
}

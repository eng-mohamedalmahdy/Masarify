package tech.lightfeather.masarify.page.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.ContactMail
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.EmptyState
import tech.lightfeather.designsystem.component.molecules.button.SegmentedButton
import tech.lightfeather.designsystem.component.organisms.listitem.MoreListItem
import tech.lightfeather.designsystem.component.organisms.listitem.MoreListItemWithSegmentedButton
import tech.lightfeather.designsystem.component.organisms.listitem.MoreListItemWithSwitch
import tech.lightfeather.designsystem.theme.AppTheme
import tech.lightfeather.domain.model.AppLanguage
import tech.lightfeather.domain.model.AppLanguages
import tech.lightfeather.masarify.app.LocalAppMainViewModel
import tech.lightfeather.masarify.component.SubscriptionStatusBanner
import tech.lightfeather.masarify.page.categories.CategoriesPage
import tech.lightfeather.masarify.page.currencies.CurrenciesPage
import tech.lightfeather.masarify.page.notificationsettings.NotificationSettingsPage

internal enum class MoreNavDestination(
    val id: String,
) {
    CURRENCY_MANAGEMENT("currency_management"),
    CATEGORY_MANAGEMENT("category_management"),
    PRIVACY_POLICY("privacy_policy"),
    CONTACT_US("contact_us"),
    RATE_US("rate_us"),
    BACKUP_RESTORE("backup_restore"),
    NOTIFICATION_SETTINGS("notification_settings"),
}

@Composable
fun MorePage(viewModel: MorePageViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.onIntent(MorePageIntent.LoadData)
    }

    MorePageContent(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun MorePageContent(
    state: MorePageState,
    onIntent: (MorePageIntent) -> Unit,
) {
    val appMainViewModel = LocalAppMainViewModel.current
    val navigator = rememberListDetailPaneScaffoldNavigator<String>()
    val coroutineScope = rememberCoroutineScope()

    BackHandler(navigator.canNavigateBack()) {
        coroutineScope.launch {
            navigator.navigateBack()
            onIntent(MorePageIntent.ClearNavigation)
        }
    }

    if (state.isLogoutAllDialogVisible) {
        AlertDialog(
            onDismissRequest = { onIntent(MorePageIntent.DismissLogoutAllDialog) },
            title = { Text(stringResource(MR.strings.logout_all_devices_confirm_title)) },
            text = { Text(stringResource(MR.strings.logout_all_devices_confirm_message)) },
            confirmButton = {
                Button(onClick = { onIntent(MorePageIntent.LogoutAllDevices) }) {
                    Text(stringResource(MR.strings.logout_all_devices))
                }
            },
            dismissButton = {
                TextButton(onClick = { onIntent(MorePageIntent.DismissLogoutAllDialog) }) {
                    Text(stringResource(MR.strings.cancel))
                }
            },
        )
    }

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            MoreListPane(
                state = state,
                onDarkThemeToggle = {
                    onIntent(MorePageIntent.ToggleDarkTheme(it))
                    appMainViewModel.toggleDarkTheme()
                },
                onBiometricToggle = {
                    onIntent(MorePageIntent.ToggleBiometric(it))
                },
                onAutoSyncRatesToggle = {
                    onIntent(MorePageIntent.ToggleAutoSyncRates(it))
                },
                onAutoSyncDataToggle = {
                    onIntent(MorePageIntent.ToggleAutoSyncData(it))
                },
                onSyncNowClick = {
                    onIntent(MorePageIntent.SyncNow)
                },
                onToggleFailedExpanded = {
                    onIntent(MorePageIntent.ToggleFailedExpanded)
                },
                onRetrySyncEntry = { id ->
                    onIntent(MorePageIntent.RetrySyncEntry(id))
                },
                onRetryAllFailed = {
                    onIntent(MorePageIntent.RetryAllFailed)
                },
                onDeleteFailedEntry = { id ->
                    onIntent(MorePageIntent.DeleteFailedEntry(id))
                },
                onDeleteAllFailed = {
                    onIntent(MorePageIntent.DeleteAllFailed)
                },
                onSignInClick = {
                    onIntent(MorePageIntent.NavigateToSignIn)
                },
                onLanguageSelected = {
                    onIntent(MorePageIntent.SelectLanguage(it))
                    appMainViewModel.changeLanguage(it)
                },
                onCurrencyManagementClick = {
                    coroutineScope.launch {
                        onIntent(MorePageIntent.NavigationIntent.SelectCurrencyManagementDetail)
                        navigator.navigateTo(
                            androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole.Detail,
                            MoreNavDestination.CURRENCY_MANAGEMENT.id,
                        )
                    }
                },
                onCategoryManagementClick = {
                    coroutineScope.launch {
                        onIntent(MorePageIntent.NavigationIntent.SelectCategoryManagementDetail)
                        navigator.navigateTo(
                            androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole.Detail,
                            MoreNavDestination.CATEGORY_MANAGEMENT.id,
                        )
                    }
                },
                onPrivacyPolicyClick = {
                    coroutineScope.launch {
                        onIntent(MorePageIntent.NavigationIntent.SelectPrivacyPolicyDetail)
                        navigator.navigateTo(
                            androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole.Detail,
                            MoreNavDestination.PRIVACY_POLICY.id,
                        )
                    }
                },
                onContactUsClick = {
                    coroutineScope.launch {
                        onIntent(MorePageIntent.NavigationIntent.SelectContactUsDetail)
                        navigator.navigateTo(
                            androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole.Detail,
                            MoreNavDestination.CONTACT_US.id,
                        )
                    }
                },
                onRateUsClick = {
                    coroutineScope.launch {
                        onIntent(MorePageIntent.NavigationIntent.SelectRateUsDetail)
                        navigator.navigateTo(
                            ListDetailPaneScaffoldRole.Detail,
                            MoreNavDestination.RATE_US.id,
                        )
                    }
                },
                onBackupRestoreClick = {
                    coroutineScope.launch {
                        onIntent(MorePageIntent.NavigationIntent.SelectBackupRestoreDetail)
                        navigator.navigateTo(
                            ListDetailPaneScaffoldRole.Detail,
                            MoreNavDestination.BACKUP_RESTORE.id,
                        )
                    }
                },
                onNotificationSettingsClick = {
                    coroutineScope.launch {
                        onIntent(MorePageIntent.NavigationIntent.SelectNotificationSettingsDetail)
                        navigator.navigateTo(
                            ListDetailPaneScaffoldRole.Detail,
                            MoreNavDestination.NOTIFICATION_SETTINGS.id,
                        )
                    }
                },
                onLogoutClick = { onIntent(MorePageIntent.Logout) },
                onLogoutAllClick = { onIntent(MorePageIntent.ShowLogoutAllDialog) },
                onResendVerificationClick = { onIntent(MorePageIntent.ResendVerification) },
                onUpgradeClick = { onIntent(MorePageIntent.NavigateToPaywall) },
            )
        },
        detailPane = {
            MoreDetailPane(
                currentDestination = navigator.currentDestination?.contentKey,
                state = state,
                onIntent = onIntent,
            )
        },
    )
}

@Composable
private fun MoreDetailPane(
    currentDestination: String?,
    state: MorePageState,
    onIntent: (MorePageIntent) -> Unit,
) {
    when (currentDestination) {
        MoreNavDestination.CURRENCY_MANAGEMENT.id -> {
            key("currency_detail") { CurrencyManagementDetailPane() }
        }
        MoreNavDestination.PRIVACY_POLICY.id -> {
            key("privacy_detail") { PrivacyPolicyDetailPane() }
        }
        MoreNavDestination.CONTACT_US.id -> {
            key("contact_detail") { ContactUsDetailPane() }
        }
        MoreNavDestination.RATE_US.id -> {
            key("rate_detail") { RateUsDetailPane() }
        }
        MoreNavDestination.CATEGORY_MANAGEMENT.id -> {
            key("category_detail") { CategoryManagementDetailPane() }
        }
        MoreNavDestination.BACKUP_RESTORE.id -> {
            key("backup_restore_detail") {
                BackupRestorePane(state = state, onIntent = onIntent)
            }
        }
        MoreNavDestination.NOTIFICATION_SETTINGS.id -> {
            key("notification_settings_detail") { NotificationSettingsPage() }
        }
        else -> {
            EmptyState(
                title = stringResource(MR.strings.select_option),
                message = stringResource(MR.strings.select_option_message),
                icon = Icons.Outlined.Settings,
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}

@Suppress("LongMethod", "LongParameterList") // Settings list composable — each item is a declarative list entry
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ThreePaneScaffoldPaneScope.MoreListPane(
    state: MorePageState,
    onDarkThemeToggle: (Boolean) -> Unit,
    onBiometricToggle: (Boolean) -> Unit,
    onAutoSyncRatesToggle: (Boolean) -> Unit,
    onAutoSyncDataToggle: (Boolean) -> Unit,
    onSyncNowClick: () -> Unit,
    onToggleFailedExpanded: () -> Unit,
    onRetrySyncEntry: (Long) -> Unit,
    onRetryAllFailed: () -> Unit,
    onDeleteFailedEntry: (Long) -> Unit,
    onDeleteAllFailed: () -> Unit,
    onSignInClick: () -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
    onCurrencyManagementClick: () -> Unit,
    onCategoryManagementClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onContactUsClick: () -> Unit,
    onRateUsClick: () -> Unit,
    onBackupRestoreClick: () -> Unit,
    onNotificationSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onLogoutAllClick: () -> Unit,
    onResendVerificationClick: () -> Unit,
    onUpgradeClick: () -> Unit,
) {
    AnimatedPane {
        MoreListPaneContent(
            state = state,
            onDarkThemeToggle = onDarkThemeToggle,
            onBiometricToggle = onBiometricToggle,
            onAutoSyncRatesToggle = onAutoSyncRatesToggle,
            onAutoSyncDataToggle = onAutoSyncDataToggle,
            onSyncNowClick = onSyncNowClick,
            onToggleFailedExpanded = onToggleFailedExpanded,
            onRetrySyncEntry = onRetrySyncEntry,
            onRetryAllFailed = onRetryAllFailed,
            onDeleteFailedEntry = onDeleteFailedEntry,
            onDeleteAllFailed = onDeleteAllFailed,
            onSignInClick = onSignInClick,
            onLanguageSelected = onLanguageSelected,
            onCurrencyManagementClick = onCurrencyManagementClick,
            onCategoryManagementClick = onCategoryManagementClick,
            onPrivacyPolicyClick = onPrivacyPolicyClick,
            onContactUsClick = onContactUsClick,
            onRateUsClick = onRateUsClick,
            onBackupRestoreClick = onBackupRestoreClick,
            onNotificationSettingsClick = onNotificationSettingsClick,
            onLogoutClick = onLogoutClick,
            onLogoutAllClick = onLogoutAllClick,
            onResendVerificationClick = onResendVerificationClick,
            onUpgradeClick = onUpgradeClick,
        )
    }
}

@Suppress("LongMethod", "LongParameterList") // Settings list composable — each item is a declarative list entry
@Composable
internal fun MoreListPaneContent(
    state: MorePageState,
    onDarkThemeToggle: (Boolean) -> Unit,
    onBiometricToggle: (Boolean) -> Unit,
    onAutoSyncRatesToggle: (Boolean) -> Unit,
    onAutoSyncDataToggle: (Boolean) -> Unit,
    onSyncNowClick: () -> Unit,
    onToggleFailedExpanded: () -> Unit,
    onRetrySyncEntry: (Long) -> Unit,
    onRetryAllFailed: () -> Unit,
    onDeleteFailedEntry: (Long) -> Unit,
    onDeleteAllFailed: () -> Unit,
    onSignInClick: () -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
    onCurrencyManagementClick: () -> Unit,
    onCategoryManagementClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onContactUsClick: () -> Unit,
    onRateUsClick: () -> Unit,
    onBackupRestoreClick: () -> Unit,
    onNotificationSettingsClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onLogoutAllClick: () -> Unit,
    onResendVerificationClick: () -> Unit,
    onUpgradeClick: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.tiny),
    ) {
        // Header
        item {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.dimens.spacing.padding.medium),
            ) {
                Text(
                    text = stringResource(MR.strings.more),
                    modifier = Modifier.semantics { heading() },
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.small))
            }
        }

        // Settings Section
        item {
            SectionHeader(stringResource(MR.strings.settings))
        }

        item {
            MoreListItemWithSwitch(
                text = stringResource(MR.strings.dark_theme),
                image = Icons.Default.DarkMode,
                checked = state.isDarkTheme,
                onCheckedChange = onDarkThemeToggle,
                modifier = Modifier.testTag("more_dark_theme_switch"),
                contentDescription = stringResource(MR.strings.dark_theme_description),
            )
        }

        item {
            MoreListItemWithSwitch(
                text = stringResource(MR.strings.biometric_settings),
                image = Icons.Default.Fingerprint,
                checked = state.isBiometricEnabled,
                onCheckedChange = onBiometricToggle,
                contentDescription = stringResource(MR.strings.biometric_settings_description),
            )
        }

        item {
            MoreListItem(
                text = stringResource(MR.strings.notification_settings),
                image = Icons.Default.Notifications,
                onClick = onNotificationSettingsClick,
                contentDescription = stringResource(MR.strings.notification_settings),
            )
        }

        item {
            MoreListItemWithSwitch(
                text = stringResource(MR.strings.auto_sync_rates),
                image = Icons.Default.Sync,
                checked = state.isAutoSyncRatesEnabled,
                onCheckedChange = onAutoSyncRatesToggle,
                contentDescription = stringResource(MR.strings.auto_sync_rates_description),
            )
        }

        item {
            MoreListItemWithSwitch(
                text = stringResource(MR.strings.auto_sync_data),
                image = Icons.Default.Sync,
                checked = state.isAutoSyncDataEnabled,
                onCheckedChange = onAutoSyncDataToggle,
                contentDescription = stringResource(MR.strings.auto_sync_data_description),
            )
        }

        item {
            MoreListItemWithSegmentedButton(
                text = "",
                image = Icons.Default.Language,
                segmentedItems =
                    state.availableLanguages.map { language ->
                        SegmentedButton.Item(
                            text = language.languageName,
                            value = language.code,
                        )
                    },
                selectedValue = state.selectedLanguage.code,
                onSelectionChanged = { selectedCode ->
                    val selectedLanguage = AppLanguages.fromCode(selectedCode)
                    onLanguageSelected(selectedLanguage)
                },
                contentDescription = stringResource(MR.strings.language_description),
            )
        }

        // Subscription Section
        if (!state.isProActive) {
            item {
                SubscriptionStatusBanner(onUpgradeClick = onUpgradeClick)
            }
        } else {
            item {
                SectionHeader(stringResource(MR.strings.subscription_section_title))
            }
            item {
                MoreListItem(
                    text = stringResource(MR.strings.subscription_manage),
                    image = Icons.Default.WorkspacePremium,
                    onClick = onUpgradeClick,
                    contentDescription = stringResource(MR.strings.subscription_manage),
                )
            }
        }

        // Management Section
        item {
            SectionHeader(stringResource(MR.strings.management))
        }

        item {
            MoreListItem(
                text = stringResource(MR.strings.category_management),
                image = Icons.Default.Category,
                onClick = onCategoryManagementClick,
                contentDescription = stringResource(MR.strings.category_management_description),
            )
        }

        item {
            MoreListItem(
                text = stringResource(MR.strings.currency_management),
                image = Icons.Default.CurrencyExchange,
                onClick = onCurrencyManagementClick,
                contentDescription = stringResource(MR.strings.currency_management_description),
            )
        }

        // Data Section
        item {
            SectionHeader(stringResource(MR.strings.data))
        }

        item {
            MoreListItem(
                text = stringResource(MR.strings.backup_restore),
                image = Icons.Default.Storage,
                onClick = onBackupRestoreClick,
                contentDescription = stringResource(MR.strings.backup_restore_description),
            )
        }

        item {
            MoreListItem(
                text = stringResource(MR.strings.sync_now),
                image = Icons.Default.Sync,
                onClick = if (state.isSyncing) null else onSyncNowClick,
                contentDescription = stringResource(MR.strings.sync_now_description),
                trailingContent = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.spacedBy(
                                AppTheme.dimens.spacing.padding.small,
                            ),
                    ) {
                        if (state.failedSyncCount > 0) {
                            Badge(
                                containerColor = MaterialTheme.colorScheme.error,
                            ) {
                                Text(state.failedSyncCount.toString())
                            }
                        }
                        if (state.isSyncing) {
                            CircularProgressIndicator()
                        }
                    }
                },
            )
        }

        if (state.failedSyncCount > 0) {
            item {
                MoreListItem(
                    text = stringResource(MR.strings.sync_failed_entries),
                    image = Icons.Default.ErrorOutline,
                    onClick = onToggleFailedExpanded,
                    trailingContent = {
                        Icon(
                            imageVector =
                                if (state.isFailedExpanded) {
                                    Icons.Default.ExpandLess
                                } else {
                                    Icons.Default.ExpandMore
                                },
                            contentDescription = null,
                        )
                    },
                )
            }

            if (state.isFailedExpanded) {
                item {
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = AppTheme.dimens.spacing.padding.medium,
                                ),
                        horizontalArrangement =
                            Arrangement.spacedBy(
                                AppTheme.dimens.spacing.padding.small,
                            ),
                    ) {
                        TextButton(onClick = onRetryAllFailed) {
                            Text(stringResource(MR.strings.sync_retry_all))
                        }
                        TextButton(onClick = onDeleteAllFailed) {
                            Text(
                                stringResource(MR.strings.sync_clear_all),
                                color = MaterialTheme.colorScheme.error,
                            )
                        }
                    }
                }

                items(state.failedEntries.size) { index ->
                    val entry = state.failedEntries[index]
                    FailedSyncEntryRow(
                        entry = entry,
                        onRetry = { onRetrySyncEntry(entry.id) },
                        onDelete = { onDeleteFailedEntry(entry.id) },
                    )
                }
            }
        }

        // Support Section
        item {
            SectionHeader(stringResource(MR.strings.support))
        }

        item {
            MoreListItem(
                text = stringResource(MR.strings.privacy_policy),
                image = Icons.Default.PrivacyTip,
                onClick = onPrivacyPolicyClick,
                contentDescription = stringResource(MR.strings.privacy_policy_description),
            )
        }

        item {
            MoreListItem(
                text = stringResource(MR.strings.contact_us),
                image = Icons.Default.ContactMail,
                onClick = onContactUsClick,
                contentDescription = stringResource(MR.strings.contact_us_description),
            )
        }

        item {
            MoreListItem(
                text = stringResource(MR.strings.rate_us),
                image = Icons.Default.Star,
                onClick = onRateUsClick,
                contentDescription = stringResource(MR.strings.rate_us_description),
            )
        }

        // Email verification banner
        if (state.isAuthenticated && !state.isEmailVerified) {
            item {
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AppTheme.dimens.spacing.padding.medium),
                    colors =
                        CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer,
                        ),
                ) {
                    Column(
                        modifier = Modifier.padding(AppTheme.dimens.spacing.padding.medium),
                        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
                    ) {
                        Text(
                            text = stringResource(MR.strings.verify_email_banner_title),
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            text = stringResource(MR.strings.verify_email_banner_message),
                            style = MaterialTheme.typography.bodySmall,
                        )
                        TextButton(onClick = onResendVerificationClick) {
                            Text(stringResource(MR.strings.verify_email_resend))
                        }
                    }
                }
            }
        }

        // Account Section
        item {
            SectionHeader(stringResource(MR.strings.accounts))
        }

        if (state.isAuthenticated) {
            item {
                MoreListItem(
                    text = stringResource(MR.strings.logout),
                    image = Icons.Default.Logout,
                    onClick = onLogoutClick,
                    contentDescription = stringResource(MR.strings.logout),
                )
            }
            item {
                MoreListItem(
                    text = stringResource(MR.strings.logout_all_devices),
                    image = Icons.Default.Logout,
                    onClick = onLogoutAllClick,
                    contentDescription = stringResource(MR.strings.logout_all_devices),
                )
            }
        } else {
            item {
                MoreListItem(
                    text = stringResource(MR.strings.sign_in),
                    image = Icons.Default.Login,
                    onClick = onSignInClick,
                    contentDescription = stringResource(MR.strings.sign_in),
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.primary,
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AppTheme.dimens.spacing.padding.medium,
                    vertical = AppTheme.dimens.spacing.padding.small,
                ),
    )
}

@Composable
private fun CurrencyManagementDetailPane() {
    CurrenciesPage()
}

@Composable
private fun PrivacyPolicyDetailPane() {
    DetailPaneWrapper(
        title = stringResource(MR.strings.privacy_policy),
    ) {
        EmptyState(
            title = stringResource(MR.strings.privacy_policy),
            message = stringResource(MR.strings.privacy_policy_content),
            icon = Icons.Default.PrivacyTip,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun ContactUsDetailPane() {
    DetailPaneWrapper(
        title = stringResource(MR.strings.contact_us),
    ) {
        EmptyState(
            title = stringResource(MR.strings.contact_us),
            message = stringResource(MR.strings.contact_us_content),
            icon = Icons.Default.ContactMail,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun RateUsDetailPane() {
    DetailPaneWrapper(
        title = stringResource(MR.strings.rate_us),
    ) {
        EmptyState(
            title = stringResource(MR.strings.rate_us),
            message = stringResource(MR.strings.rate_us_content),
            icon = Icons.Default.Star,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun CategoryManagementDetailPane() {
    CategoriesPage()
}

@Composable
private fun DetailPaneWrapper(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(AppTheme.dimens.spacing.padding.medium),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f),
            )
        }
        Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.medium))
        content()
    }
}

@Composable
private fun FailedSyncEntryRow(
    entry: tech.lightfeather.domain.model.sync.SyncQueueEntry,
    onRetry: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = AppTheme.dimens.spacing.padding.medium,
                    vertical = AppTheme.dimens.spacing.padding.tiny,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${entry.entityType} - ${entry.operation}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = stringResource(MR.strings.sync_retry_count, entry.retryCount),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        IconButton(onClick = onRetry) {
            Icon(
                Icons.Default.Refresh,
                contentDescription = stringResource(MR.strings.sync_retry),
                tint = MaterialTheme.colorScheme.primary,
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                Icons.Default.Delete,
                contentDescription = stringResource(MR.strings.delete),
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Preview
@Composable
fun MorePagePreview() {
    AppTheme {
        MorePageContent(
            state =
                MorePageState(
                    isDarkTheme = false,
                    selectedLanguage = tech.lightfeather.domain.model.AppLanguages.English,
                    availableLanguages =
                        listOf(
                            tech.lightfeather.domain.model.AppLanguages.English,
                            tech.lightfeather.domain.model.AppLanguages.Arabic,
                        ),
                ),
            onIntent = {},
        )
    }
}

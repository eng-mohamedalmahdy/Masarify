package com.lightfeather.masarify.page.more

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
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PrivacyTip
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
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
import androidx.compose.ui.text.font.FontWeight
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.EmptyState
import com.lightfeather.designsystem.component.molecules.button.SegmentedButton
import com.lightfeather.designsystem.component.organisms.listitem.MoreListItem
import com.lightfeather.designsystem.component.organisms.listitem.MoreListItemWithSegmentedButton
import com.lightfeather.designsystem.component.organisms.listitem.MoreListItemWithSwitch
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.domain.model.AppLanguage
import com.lightfeather.masarify.app.LocalAppMainViewModel
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

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
    val navigator = rememberListDetailPaneScaffoldNavigator<MorePageIntent.NavigationIntent>()
    val coroutineScope = rememberCoroutineScope()

    BackHandler(navigator.canNavigateBack()) {
        coroutineScope.launch {
            onIntent(MorePageIntent.ClearNavigation(navigator))
        }
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
                onLanguageSelected = {
                    onIntent(MorePageIntent.SelectLanguage(it))
                    appMainViewModel.changeLanguage(it)
                },
                onCurrencyManagementClick = {
                    coroutineScope.launch {
                        onIntent(MorePageIntent.NavigationIntent.SelectCurrencyManagementDetail(navigator))
                    }
                },
                onCategoryManagementClick = {
                    onIntent(MorePageIntent.NavigateToCategoryManagement)
                },
                onPrivacyPolicyClick = {
                    coroutineScope.launch {
                        onIntent(MorePageIntent.NavigationIntent.SelectPrivacyPolicyDetail(navigator))
                    }
                },
                onContactUsClick = {
                    coroutineScope.launch {
                        onIntent(MorePageIntent.NavigationIntent.SelectContactUsDetail(navigator))
                    }
                },
                onRateUsClick = {
                    coroutineScope.launch {
                        onIntent(MorePageIntent.NavigationIntent.SelectRateUsDetail(navigator))
                    }
                },
            )
        },
        detailPane = {
            val currentDestination = navigator.currentDestination?.contentKey
            when (val intent = currentDestination) {
                is MorePageIntent.NavigationIntent.SelectCurrencyManagementDetail -> {
                    key("currency_detail") {
                        CurrencyManagementDetailPane(
                            onBack = { onIntent(MorePageIntent.ClearNavigation(navigator)) },
                        )
                    }
                }

                is MorePageIntent.NavigationIntent.SelectPrivacyPolicyDetail -> {
                    key("privacy_detail") {
                        PrivacyPolicyDetailPane(
                            onBack = { onIntent(MorePageIntent.ClearNavigation(navigator)) },
                        )
                    }
                }

                is MorePageIntent.NavigationIntent.SelectContactUsDetail -> {
                    key("contact_detail") {
                        ContactUsDetailPane(
                            onBack = { onIntent(MorePageIntent.ClearNavigation(navigator)) },
                        )
                    }
                }

                is MorePageIntent.NavigationIntent.SelectRateUsDetail -> {
                    key("rate_detail") {
                        RateUsDetailPane(
                            onBack = { onIntent(MorePageIntent.ClearNavigation(navigator)) },
                        )
                    }
                }

                null -> {
                    EmptyState(
                        title = stringResource(MR.strings.select_option),
                        message = stringResource(MR.strings.select_option_message),
                        icon = Icons.Outlined.Settings,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ThreePaneScaffoldPaneScope.MoreListPane(
    state: MorePageState,
    onDarkThemeToggle: (Boolean) -> Unit,
    onLanguageSelected: (AppLanguage) -> Unit,
    onCurrencyManagementClick: () -> Unit,
    onCategoryManagementClick: () -> Unit,
    onPrivacyPolicyClick: () -> Unit,
    onContactUsClick: () -> Unit,
    onRateUsClick: () -> Unit,
) {
    AnimatedPane {
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
                    contentDescription = stringResource(MR.strings.dark_theme_description),
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
                        val selectedLanguage = AppLanguage.fromCode(selectedCode)
                        onLanguageSelected(selectedLanguage)
                    },
                    contentDescription = stringResource(MR.strings.language_description),
                )
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

// Detail Pane Composables
@Composable
private fun CurrencyManagementDetailPane(onBack: () -> Unit) {
    DetailPaneWrapper(
        title = stringResource(MR.strings.currency_management),
        onBack = onBack,
    ) {
        EmptyState(
            title = stringResource(MR.strings.coming_soon),
            message = stringResource(MR.strings.currency_management_coming_soon),
            icon = Icons.Default.CurrencyExchange,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun PrivacyPolicyDetailPane(onBack: () -> Unit) {
    DetailPaneWrapper(
        title = stringResource(MR.strings.privacy_policy),
        onBack = onBack,
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
private fun ContactUsDetailPane(onBack: () -> Unit) {
    DetailPaneWrapper(
        title = stringResource(MR.strings.contact_us),
        onBack = onBack,
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
private fun RateUsDetailPane(onBack: () -> Unit) {
    DetailPaneWrapper(
        title = stringResource(MR.strings.rate_us),
        onBack = onBack,
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
private fun DetailPaneWrapper(
    title: String,
    onBack: () -> Unit,
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

@Preview
@Composable
fun MorePagePreview() {
    AppTheme {
        MorePageContent(
            state =
                MorePageState(
                    isDarkTheme = false,
                    selectedLanguage = com.lightfeather.domain.model.AppLanguage.English,
                    availableLanguages =
                        listOf(
                            com.lightfeather.domain.model.AppLanguage.English,
                            com.lightfeather.domain.model.AppLanguage.Arabic,
                        ),
                ),
            onIntent = {},
        )
    }
}

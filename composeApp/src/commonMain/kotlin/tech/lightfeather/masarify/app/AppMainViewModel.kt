package tech.lightfeather.masarify.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.github.aakira.napier.Napier
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import tech.lightfeather.domain.model.AppLanguage
import tech.lightfeather.domain.model.AppLanguages
import tech.lightfeather.domain.repository.UserRepository
import tech.lightfeather.domain.usecase.GetAllAccounts
import tech.lightfeather.domain.usecase.GetExpenseCategoriesByUsage
import tech.lightfeather.domain.usecase.SeedDefaultCategories
import tech.lightfeather.domain.usecase.SyncRemoteExchangeRatesUseCase
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.navigation.routes.DashboardRoute
import tech.lightfeather.masarify.widget.WidgetCategory
import tech.lightfeather.masarify.widget.WidgetDataSyncService

class AppMainViewModel(
    private val navigator: Navigator,
    private val userDataRepository: UserRepository,
    private val getAllAccounts: GetAllAccounts,
    private val seedDefaultCategories: SeedDefaultCategories,
    private val getExpenseCategoriesByUsage: GetExpenseCategoriesByUsage,
    private val widgetDataSyncService: WidgetDataSyncService,
    private val syncRemoteExchangeRates: SyncRemoteExchangeRatesUseCase,
) : ViewModel() {
    init {
        viewModelScope.launch {
            if (userDataRepository.isAutoSyncRatesEnabled()) {
                syncRemoteExchangeRates().fold(
                    onFailure = { Napier.e("Failed to sync remote exchange rates: $it") },
                )
            }
        }
        viewModelScope.launch {
            seedDefaultCategories().foldSuspend(
                onSuccess = { success ->
                    if (success) Napier.d("Default categories seeded successfully")
                },
                onFailure = { error ->
                    Napier.e("Failed to seed default categories: $error")
                },
            )
        }
        viewModelScope.launch { syncWidgetCategories() }
    }

    private suspend fun syncWidgetCategories() {
        getExpenseCategoriesByUsage(MAX_WIDGET_CATEGORIES).foldSuspend(
            onSuccess = { categoriesFlow ->
                categoriesFlow.collect { categories ->
                    val widgetCategories =
                        categories.map { category ->
                            WidgetCategory(
                                id = category.id.toString(),
                                name = category.name,
                                color = category.color,
                                icon = category.icon,
                                resourceKey = category.resourceKey,
                            )
                        }
                    widgetDataSyncService.syncExpenseCategories(widgetCategories)
                }
            },
            onFailure = { error ->
                Napier.e("Failed to sync widget categories: $error")
            },
        )
    }

    companion object {
        private const val MAX_WIDGET_CATEGORIES = 5
    }

    private val _darkTheme = MutableStateFlow(false)
    val darkTheme =
        _darkTheme
            .onStart {
                val isDarkMode = userDataRepository.isDarkMode()
                _darkTheme.value = isDarkMode
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false,
            )

    private val _dynamicColor = MutableStateFlow(false)
    val dynamicColor =
        _dynamicColor
            .onStart {
                val isDynamicColor = userDataRepository.isDynamicColors()
                _dynamicColor.value = isDynamicColor
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = false,
            )
    private val _currentLanguage = MutableStateFlow<AppLanguage>(AppLanguages.English)

    val initialDataSaved: Flow<Boolean> =
        getAllAccounts()
            .flatMap { accountsFlow ->
                userDataRepository.getUserData().map { userData ->
                    accountsFlow.map { accounts ->
                        Napier.d("Accounts: ${accounts.size}, UserData: $userData")
                        accounts.isNotEmpty() && userData != null
                    }
                }
            }.foldResult(
                onSuccess = { it },
                onFailure = { flowOf(false) },
            )
    val currentLanguage =
        _currentLanguage
            .onStart {
                val language = userDataRepository.getAppLanguage()
                _currentLanguage.value = language ?: AppLanguages.English
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = AppLanguages.English,
            )

    fun isBiometricEnabled(): Boolean = userDataRepository.isBiometricEnabled()

    fun toggleDarkTheme() {
        viewModelScope.launch {
            userDataRepository.toggleDarkMode()
            _darkTheme.value = !darkTheme.value
        }
    }

    fun changeLanguage(language: AppLanguage) {
        viewModelScope.launch {
            userDataRepository.setAppLanguage(language)
            _currentLanguage.value = language
            delay(200)
            navigator.navigateAndClearBackStack(DashboardRoute)
        }
    }
}

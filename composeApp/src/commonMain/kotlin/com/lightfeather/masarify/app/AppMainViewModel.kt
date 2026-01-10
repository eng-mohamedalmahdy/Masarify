package com.lightfeather.masarify.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lightfeather.domain.model.AppLanguage
import com.lightfeather.domain.model.AppLanguages
import com.lightfeather.domain.repository.UserRepository
import com.lightfeather.domain.usecase.GetAllAccounts
import com.lightfeather.masarify.navigation.Navigator
import com.lightfeather.masarify.navigation.routes.DashboardRoute
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

class AppMainViewModel(
    private val navigator: Navigator,
    private val userDataRepository: UserRepository,
    private val getAllAccounts: GetAllAccounts,
) : ViewModel() {
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

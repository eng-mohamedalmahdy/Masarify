package ui.main

import com.russhwolf.settings.Settings
import data.local.getLanguage
import data.local.isDarkModeEnabled
import data.local.setDarkMode
import data.local.setLanguage
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val settings by lazy { Settings() }
    private val ioDispatchers = SupervisorJob() + Dispatchers.IO

    private val _isSystemDarkMode = MutableStateFlow(settings.isDarkModeEnabled())
    val isSystemDarkMode = _isSystemDarkMode.asStateFlow()

    private val _currentLanguage = MutableStateFlow(settings.getLanguage())
    val currentLanguage = _currentLanguage.asStateFlow()

    fun setDarkMode(isDarkMode: Boolean) = viewModelScope.launch(ioDispatchers) {
        settings.setDarkMode(isDarkMode)
        _isSystemDarkMode.emit(settings.isDarkModeEnabled())
    }

    fun setCurrentLanguage(language: String) = viewModelScope.launch(ioDispatchers) {
        settings.setLanguage(language)
        _currentLanguage.emit(settings.getLanguage())
    }

}
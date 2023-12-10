package ui.main

import com.russhwolf.settings.Settings
import data.local.isDarkModeEnabled
import data.local.setDarkMode
import dev.icerock.moko.mvvm.viewmodel.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    val settings by lazy { Settings() }
    private val _isSystemDarkMode = MutableStateFlow(settings.isDarkModeEnabled())
    private val ioDispatchers = SupervisorJob() + Dispatchers.IO

    val isSystemDarkMode = _isSystemDarkMode.asStateFlow()

    fun setDarkMode(isDarkMode: Boolean) = viewModelScope.launch(ioDispatchers) {
        settings.setDarkMode(isDarkMode)
        _isSystemDarkMode.emit(settings.isDarkModeEnabled())
    }
}
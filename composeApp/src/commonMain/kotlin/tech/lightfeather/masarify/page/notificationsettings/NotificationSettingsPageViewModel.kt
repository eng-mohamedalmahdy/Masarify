package tech.lightfeather.masarify.page.notificationsettings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import tech.lightfeather.data.util.IoDispatcher
import tech.lightfeather.domain.model.NotificationSettings
import tech.lightfeather.domain.model.ReminderType
import tech.lightfeather.domain.usecase.GetNotificationSettings
import tech.lightfeather.domain.usecase.UpdateNotificationSettings
import tech.lightfeather.masarify.notification.NotificationScheduler

internal class NotificationSettingsPageViewModel(
    private val getNotificationSettings: GetNotificationSettings,
    private val updateNotificationSettings: UpdateNotificationSettings,
) : ViewModel() {
    private val _state = MutableStateFlow(NotificationSettingsPageState())
    internal val state: StateFlow<NotificationSettingsPageState> = _state

    internal fun onIntent(intent: NotificationSettingsPageIntent) {
        when (intent) {
            is NotificationSettingsPageIntent.LoadData -> loadSettings()
            is NotificationSettingsPageIntent.ToggleMasterReminders -> toggleMaster(intent.enabled)
            is NotificationSettingsPageIntent.ToggleReminderType -> toggleType(intent.type, intent.enabled)
            is NotificationSettingsPageIntent.SetDailyReminderTime -> setDailyTime(intent.minutes)
            is NotificationSettingsPageIntent.UpdatePermissionState -> {
                _state.value = _state.value.copy(isPermissionGranted = intent.granted)
                if (intent.granted) {
                    viewModelScope.launch(Dispatchers.IoDispatcher) {
                        NotificationScheduler.scheduleAllReminders(_state.value.settings)
                    }
                }
            }
        }
    }

    private fun loadSettings() {
        _state.value = _state.value.copy(settings = getNotificationSettings())
    }

    private fun toggleMaster(enabled: Boolean) {
        val updated = _state.value.settings.copy(isRemindersEnabled = enabled)
        updateNotificationSettings(updated)
        _state.value = _state.value.copy(settings = updated)
        if (enabled) {
            viewModelScope.launch(Dispatchers.IoDispatcher) {
                NotificationScheduler.scheduleAllReminders(updated)
            }
        } else {
            NotificationScheduler.cancelAllReminders()
        }
    }

    private fun toggleType(
        type: ReminderType,
        enabled: Boolean,
    ) {
        persistAndSchedule(applyTypeToggle(_state.value.settings, type, enabled))
    }

    private fun setDailyTime(minutes: Int) {
        persistAndSchedule(_state.value.settings.copy(dailyReminderMinutes = minutes))
    }

    private fun persistAndSchedule(updated: NotificationSettings) {
        updateNotificationSettings(updated)
        viewModelScope.launch(Dispatchers.IoDispatcher) {
            NotificationScheduler.scheduleAllReminders(updated)
        }
        _state.value = _state.value.copy(settings = updated)
    }

    private fun applyTypeToggle(
        settings: NotificationSettings,
        type: ReminderType,
        enabled: Boolean,
    ) = when (type) {
        ReminderType.DAILY_EXPENSE_LOG -> settings.copy(isDailyExpenseLogEnabled = enabled)
        ReminderType.WEEKLY_SUMMARY -> settings.copy(isWeeklySummaryEnabled = enabled)
        ReminderType.IDLE_RE_ENGAGEMENT -> settings.copy(isIdleReEngagementEnabled = enabled)
        ReminderType.MONTHLY_RECAP -> settings.copy(isMonthlyRecapEnabled = enabled)
    }
}

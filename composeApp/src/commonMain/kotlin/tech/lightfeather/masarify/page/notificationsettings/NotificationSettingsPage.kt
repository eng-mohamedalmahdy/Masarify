package tech.lightfeather.masarify.page.notificationsettings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.organisms.listitem.MoreListItem
import tech.lightfeather.designsystem.component.organisms.listitem.MoreListItemWithSwitch
import tech.lightfeather.designsystem.theme.AppTheme
import tech.lightfeather.domain.model.ReminderType
import tech.lightfeather.masarify.notification.rememberNotificationPermissionRequester

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun NotificationSettingsPage(viewModel: NotificationSettingsPageViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    val permissionRequester = rememberNotificationPermissionRequester()
    var showTimePicker by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.onIntent(NotificationSettingsPageIntent.LoadData)
        viewModel.onIntent(
            NotificationSettingsPageIntent.UpdatePermissionState(permissionRequester.isPermissionGranted()),
        )
    }

    if (showTimePicker) {
        NotificationTimePickerDialog(
            initialMinutes = state.settings.dailyReminderMinutes,
            onConfirm = { minutes ->
                viewModel.onIntent(NotificationSettingsPageIntent.SetDailyReminderTime(minutes))
                showTimePicker = false
            },
            onDismiss = { showTimePicker = false },
        )
    }

    NotificationSettingsContent(
        state = state,
        onToggleMaster = { viewModel.onIntent(NotificationSettingsPageIntent.ToggleMasterReminders(it)) },
        onToggleType = { type, enabled ->
            viewModel.onIntent(NotificationSettingsPageIntent.ToggleReminderType(type, enabled))
        },
        onDailyTimeClick = { showTimePicker = true },
        onGrantPermission = {
            permissionRequester.requestPermission { granted ->
                viewModel.onIntent(NotificationSettingsPageIntent.UpdatePermissionState(granted))
            }
        },
    )
}

@Suppress("LongMethod") // Declarative settings list — each item is one entry
@Composable
private fun NotificationSettingsContent(
    state: NotificationSettingsPageState,
    onToggleMaster: (Boolean) -> Unit,
    onToggleType: (ReminderType, Boolean) -> Unit,
    onDailyTimeClick: () -> Unit,
    onGrantPermission: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.tiny),
    ) {
        item {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.dimens.spacing.padding.medium),
            ) {
                Text(
                    text = stringResource(MR.strings.notification_settings),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.small))
            }
        }

        if (!state.isPermissionGranted) {
            item {
                MoreListItem(
                    text = stringResource(MR.strings.notifications_grant_permission),
                    image = Icons.Default.Notifications,
                    onClick = onGrantPermission,
                    contentDescription = stringResource(MR.strings.notifications_permission_required),
                )
            }
        }

        item {
            MoreListItemWithSwitch(
                text = stringResource(MR.strings.notifications_master_toggle),
                image = Icons.Default.Notifications,
                checked = state.settings.isRemindersEnabled,
                onCheckedChange = onToggleMaster,
            )
        }

        item {
            MoreListItemWithSwitch(
                text = stringResource(MR.strings.notifications_daily_expense_log),
                image = Icons.Default.CalendarToday,
                checked = state.settings.isDailyExpenseLogEnabled,
                onCheckedChange = { onToggleType(ReminderType.DAILY_EXPENSE_LOG, it) },
                contentDescription = stringResource(MR.strings.notifications_daily_expense_log_description),
                enabled = state.settings.isRemindersEnabled,
            )
        }

        item {
            MoreListItemWithSwitch(
                text = stringResource(MR.strings.notifications_weekly_summary),
                image = Icons.Default.DateRange,
                checked = state.settings.isWeeklySummaryEnabled,
                onCheckedChange = { onToggleType(ReminderType.WEEKLY_SUMMARY, it) },
                contentDescription = stringResource(MR.strings.notifications_weekly_summary_description),
                enabled = state.settings.isRemindersEnabled,
            )
        }

        item {
            MoreListItemWithSwitch(
                text = stringResource(MR.strings.notifications_idle_re_engagement),
                image = Icons.Default.Timer,
                checked = state.settings.isIdleReEngagementEnabled,
                onCheckedChange = { onToggleType(ReminderType.IDLE_RE_ENGAGEMENT, it) },
                contentDescription = stringResource(MR.strings.notifications_idle_re_engagement_description),
                enabled = state.settings.isRemindersEnabled,
            )
        }

        item {
            MoreListItemWithSwitch(
                text = stringResource(MR.strings.notifications_monthly_recap),
                image = Icons.Default.CalendarMonth,
                checked = state.settings.isMonthlyRecapEnabled,
                onCheckedChange = { onToggleType(ReminderType.MONTHLY_RECAP, it) },
                contentDescription = stringResource(MR.strings.notifications_monthly_recap_description),
                enabled = state.settings.isRemindersEnabled,
            )
        }

        item {
            val isTimePickerEnabled =
                state.settings.isRemindersEnabled && state.settings.isDailyExpenseLogEnabled
            val hour = state.settings.dailyReminderMinutes / 60
            val minute = state.settings.dailyReminderMinutes % 60
            MoreListItem(
                text = stringResource(MR.strings.notifications_daily_time),
                image = Icons.Default.Schedule,
                onClick = if (isTimePickerEnabled) onDailyTimeClick else null,
                contentDescription = stringResource(MR.strings.notifications_daily_time),
                trailingContent = {
                    Text(
                        text = "${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                },
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NotificationTimePickerDialog(
    initialMinutes: Int,
    onConfirm: (Int) -> Unit,
    onDismiss: () -> Unit,
) {
    val timePickerState =
        rememberTimePickerState(
            initialHour = initialMinutes / 60,
            initialMinute = initialMinutes % 60,
            is24Hour = true,
        )
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(MR.strings.notifications_daily_time)) },
        text = { TimeInput(state = timePickerState) },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(timePickerState.hour * 60 + timePickerState.minute) },
            ) {
                Text(stringResource(MR.strings.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(MR.strings.cancel))
            }
        },
    )
}

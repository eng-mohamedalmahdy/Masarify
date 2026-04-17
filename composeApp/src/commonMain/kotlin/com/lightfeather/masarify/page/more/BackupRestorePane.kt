package com.lightfeather.masarify.page.more

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.EmptyState
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.domain.model.ImportMode
import com.lightfeather.masarify.PlatformsSlugs
import com.lightfeather.masarify.asSlug
import com.lightfeather.masarify.getPlatform
import dev.icerock.moko.resources.compose.stringResource
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.FileKitType
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.name
import io.github.vinceglb.filekit.readBytes
import kotlinx.coroutines.launch

@Suppress("LongMethod")
@Composable
internal fun BackupRestorePane(
    state: MorePageState,
    onIntent: (MorePageIntent) -> Unit,
) {
    val isWeb = getPlatform().asSlug() == PlatformsSlugs.WEB

    if (isWeb) {
        EmptyState(
            title = stringResource(MR.strings.backup_not_supported_title),
            message = stringResource(MR.strings.backup_not_supported_message),
            icon = Icons.Outlined.CloudOff,
            modifier = Modifier.fillMaxSize(),
        )
        return
    }

    val scope = rememberCoroutineScope()
    var selectedFile by remember { mutableStateOf<ByteArray?>(null) }
    var selectedFileName by remember { mutableStateOf<String?>(null) }
    var selectedMode by remember { mutableStateOf(ImportMode.APPEND) }
    var showConfirmDialog by remember { mutableStateOf(false) }

    if (showConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showConfirmDialog = false },
            title = { Text(stringResource(MR.strings.import_confirm_title)) },
            text = {
                Text(
                    if (selectedMode == ImportMode.CLEAN) {
                        stringResource(MR.strings.import_confirm_clean_message)
                    } else {
                        stringResource(MR.strings.import_confirm_append_message)
                    },
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showConfirmDialog = false
                        val bytes = selectedFile ?: return@Button
                        onIntent(MorePageIntent.ImportData(bytes, selectedMode))
                    },
                ) {
                    Text(stringResource(MR.strings.import_button))
                }
            },
            dismissButton = {
                TextButton(onClick = { showConfirmDialog = false }) {
                    Text(stringResource(MR.strings.cancel))
                }
            },
        )
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(AppTheme.dimens.spacing.padding.medium),
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.medium),
    ) {
        Text(
            text = stringResource(MR.strings.backup_restore),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.small))

        // Export section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
            ) {
                Icon(
                    imageVector = Icons.Default.CloudUpload,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(MR.strings.export_data),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                text = stringResource(MR.strings.export_data_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Button(
                onClick = { onIntent(MorePageIntent.ExportData) },
                enabled = !state.isExporting && !state.isImporting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isExporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(AppTheme.dimens.icon.size.small),
                        strokeWidth = AppTheme.dimens.hairline,
                    )
                } else {
                    Text(stringResource(MR.strings.export_button))
                }
            }
        }

        HorizontalDivider()

        // Import section
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
            ) {
                Icon(
                    imageVector = Icons.Default.CloudDownload,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = stringResource(MR.strings.import_data),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        @Suppress("TooGenericExceptionCaught")
                        try {
                            val file =
                                FileKit.openFilePicker(
                                    type = FileKitType.File(extensions = listOf("masarify")),
                                    mode = FileKitMode.Single,
                                )
                            if (file != null) {
                                selectedFile = file.readBytes()
                                selectedFileName = file.name
                            }
                        } catch (_: Exception) {
                        }
                    }
                },
                enabled = !state.isImporting && !state.isExporting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(imageVector = Icons.Default.Storage, contentDescription = null)
                    Text(selectedFileName ?: stringResource(MR.strings.pick_file))
                }
            }

            // Mode selection
            Text(
                text = stringResource(MR.strings.import_mode),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            ImportModeOption(
                label = stringResource(MR.strings.import_mode_append),
                description = stringResource(MR.strings.import_mode_append_description),
                selected = selectedMode == ImportMode.APPEND,
                onSelect = { selectedMode = ImportMode.APPEND },
            )

            ImportModeOption(
                label = stringResource(MR.strings.import_mode_clean),
                description = stringResource(MR.strings.import_mode_clean_description),
                selected = selectedMode == ImportMode.CLEAN,
                onSelect = { selectedMode = ImportMode.CLEAN },
            )

            Button(
                onClick = { showConfirmDialog = true },
                enabled = selectedFile != null && !state.isImporting && !state.isExporting,
                modifier = Modifier.fillMaxWidth(),
            ) {
                if (state.isImporting) {
                    CircularProgressIndicator(
                        modifier = Modifier.height(AppTheme.dimens.icon.size.small),
                        strokeWidth = AppTheme.dimens.hairline,
                    )
                } else {
                    Text(stringResource(MR.strings.import_button))
                }
            }
        }
    }
}

@Composable
private fun ImportModeOption(
    label: String,
    description: String,
    selected: Boolean,
    onSelect: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.Top,
        modifier = Modifier.fillMaxWidth(),
    ) {
        RadioButton(
            selected = selected,
            onClick = onSelect,
        )
        Column(
            modifier =
                Modifier
                    .padding(top = AppTheme.dimens.spacing.padding.small)
                    .weight(1f),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

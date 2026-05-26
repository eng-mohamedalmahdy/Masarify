package tech.lightfeather.masarify.page.auth.verifyemail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.button.PrimaryButton
import tech.lightfeather.designsystem.theme.AppTheme

@Composable
fun VerifyEmailPage(
    token: String,
    viewModel: VerifyEmailPageViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(token) {
        viewModel.onIntent(VerifyEmailPageIntent.Verify(token))
    }

    VerifyEmailPageContent(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
internal fun VerifyEmailPageContent(
    state: VerifyEmailPageState,
    onIntent: (VerifyEmailPageIntent) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize().padding(AppTheme.dimens.default),
        contentAlignment = Alignment.Center,
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator()
            }

            state.isSuccess -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium),
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(MR.strings.verify_email_success),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(AppTheme.dimens.medium))
                    PrimaryButton(
                        onClick = { onIntent(VerifyEmailPageIntent.GoToDashboard) },
                        modifier = Modifier.testTag("verify_email_go_to_dashboard_button"),
                    ) {
                        Text(stringResource(MR.strings.go_to_dashboard))
                    }
                }
            }

            state.isError -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium),
                ) {
                    Icon(
                        imageVector = Icons.Default.ErrorOutline,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                    )
                    Text(
                        text = stringResource(MR.strings.verify_email_failure),
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(AppTheme.dimens.medium))
                    PrimaryButton(
                        onClick = { onIntent(VerifyEmailPageIntent.GoToDashboard) },
                        modifier = Modifier.testTag("verify_email_go_to_dashboard_button"),
                    ) {
                        Text(stringResource(MR.strings.go_to_dashboard))
                    }
                }
            }
        }
    }
}

package tech.lightfeather.masarify.page.auth.forgotpassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MarkEmailRead
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.TextField
import tech.lightfeather.designsystem.component.molecules.button.PrimaryButton
import tech.lightfeather.designsystem.component.molecules.button.TextButton
import tech.lightfeather.designsystem.theme.AppTheme

@Composable
fun ForgotPasswordPage(viewModel: ForgotPasswordPageViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    ForgotPasswordPageContent(state = state, onIntent = viewModel::onIntent)
}

@Suppress("LongMethod")
@Composable
internal fun ForgotPasswordPageContent(
    state: ForgotPasswordPageState,
    onIntent: (ForgotPasswordPageIntent) -> Unit,
) {
    Box(
        Modifier
            .fillMaxSize()
            .paint(
                painterResource(MR.images.bg),
                contentScale = ContentScale.FillHeight,
            ),
    ) {
        Card(
            modifier =
                Modifier
                    .padding(AppTheme.dimens.default)
                    .fillMaxWidth()
                    .align(Alignment.Center)
                    .padding(AppTheme.dimens.medium),
            colors =
                CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
        ) {
            Column(
                modifier = Modifier.padding(AppTheme.dimens.default),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium),
            ) {
                if (state.isSent) {
                    Icon(
                        imageVector = Icons.Default.MarkEmailRead,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                    Text(
                        text = stringResource(MR.strings.forgot_password_success),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(AppTheme.dimens.medium))
                    TextButton(
                        onClick = { onIntent(ForgotPasswordPageIntent.NavigateBack) },
                        modifier = Modifier.fillMaxWidth().testTag("forgot_password_back_button"),
                    ) {
                        Text(stringResource(MR.strings.back_to_login))
                    }
                } else {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = stringResource(MR.strings.forgot_password_title),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = stringResource(MR.strings.forgot_password_subtitle),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }

                    TextField(
                        value = state.email,
                        onValueChange = { onIntent(ForgotPasswordPageIntent.UpdateEmail(it)) },
                        modifier = Modifier.fillMaxWidth().testTag("forgot_password_email_field"),
                        label = stringResource(MR.strings.email),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    )

                    if (state.isLoading) {
                        CircularProgressIndicator()
                    } else {
                        PrimaryButton(
                            onClick = { onIntent(ForgotPasswordPageIntent.Submit) },
                            modifier = Modifier.fillMaxWidth().testTag("forgot_password_submit_button"),
                        ) {
                            Text(stringResource(MR.strings.forgot_password_button))
                        }
                    }

                    TextButton(
                        onClick = { onIntent(ForgotPasswordPageIntent.NavigateBack) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(stringResource(MR.strings.back_to_login))
                    }
                }
            }
        }
    }
}

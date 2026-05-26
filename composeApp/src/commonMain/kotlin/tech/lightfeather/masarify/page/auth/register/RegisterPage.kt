package tech.lightfeather.masarify.page.auth.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.text.input.PasswordVisualTransformation
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.TextField
import tech.lightfeather.designsystem.component.molecules.button.PrimaryButton
import tech.lightfeather.designsystem.component.molecules.button.TextButton
import tech.lightfeather.designsystem.theme.AppTheme

@Composable
fun RegisterPage(viewModel: RegisterPageViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    RegisterPageContent(state = state, onIntent = viewModel::onIntent)
}

@Suppress("LongMethod")
@Composable
internal fun RegisterPageContent(
    state: RegisterPageState,
    onIntent: (RegisterPageIntent) -> Unit,
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(MR.strings.register_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(MR.strings.register_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                TextField(
                    value = state.name,
                    onValueChange = { onIntent(RegisterPageIntent.UpdateName(it)) },
                    modifier = Modifier.fillMaxWidth().testTag("register_name_field"),
                    label = stringResource(MR.strings.user_name),
                )

                TextField(
                    value = state.email,
                    onValueChange = { onIntent(RegisterPageIntent.UpdateEmail(it)) },
                    modifier = Modifier.fillMaxWidth().testTag("register_email_field"),
                    label = stringResource(MR.strings.email),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )

                TextField(
                    value = state.password,
                    onValueChange = { onIntent(RegisterPageIntent.UpdatePassword(it)) },
                    modifier = Modifier.fillMaxWidth().testTag("register_password_field"),
                    label = stringResource(MR.strings.password),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                )

                TextField(
                    value = state.confirmPassword,
                    onValueChange = { onIntent(RegisterPageIntent.UpdateConfirmPassword(it)) },
                    modifier = Modifier.fillMaxWidth().testTag("register_confirm_password_field"),
                    label = stringResource(MR.strings.confirm_password),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                )

                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    PrimaryButton(
                        onClick = { onIntent(RegisterPageIntent.Submit) },
                        modifier = Modifier.fillMaxWidth().testTag("register_submit_button"),
                    ) {
                        Text(text = stringResource(MR.strings.register_button))
                    }
                }

                TextButton(
                    onClick = { onIntent(RegisterPageIntent.NavigateToLogin) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(MR.strings.have_account_prompt),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

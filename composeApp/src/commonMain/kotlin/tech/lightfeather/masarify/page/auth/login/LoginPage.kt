package tech.lightfeather.masarify.page.auth.login

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.TextField
import tech.lightfeather.designsystem.component.molecules.button.PrimaryButton
import tech.lightfeather.designsystem.component.molecules.button.TextButton
import tech.lightfeather.designsystem.theme.AppTheme
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginPage(viewModel: LoginPageViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    LoginPageContent(state = state, onIntent = viewModel::onIntent)
}

@Suppress("LongMethod")
@Composable
internal fun LoginPageContent(
    state: LoginPageState,
    onIntent: (LoginPageIntent) -> Unit,
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
                        text = stringResource(MR.strings.login_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(MR.strings.login_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                TextField(
                    value = state.email,
                    onValueChange = { onIntent(LoginPageIntent.UpdateEmail(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(MR.strings.email),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                )

                TextField(
                    value = state.password,
                    onValueChange = { onIntent(LoginPageIntent.UpdatePassword(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(MR.strings.password),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    visualTransformation = PasswordVisualTransformation(),
                )

                if (state.isLoading) {
                    CircularProgressIndicator()
                } else {
                    PrimaryButton(
                        onClick = { onIntent(LoginPageIntent.Submit) },
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        Text(text = stringResource(MR.strings.login_button))
                    }
                }

                TextButton(
                    onClick = { onIntent(LoginPageIntent.NavigateToRegister) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text = stringResource(MR.strings.no_account_prompt),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
    }
}

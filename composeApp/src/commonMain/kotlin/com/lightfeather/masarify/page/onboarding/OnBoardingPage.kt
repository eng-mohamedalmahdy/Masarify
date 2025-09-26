package com.lightfeather.masarify.page.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.KeyboardType
import com.lightfeather.designsystem.component.molecules.AppImage
import com.lightfeather.designsystem.component.molecules.TextField
import com.lightfeather.designsystem.component.molecules.button.PrimaryButton
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource
import masarify.composeapp.generated.resources.Res
import masarify.composeapp.generated.resources.bg
import masarify.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OnBoardingPage(viewModel: OnBoardingPageViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    OnBoardingPageContent(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
internal fun OnBoardingPageContent(
    state: OnBoardingPageState,
    onIntent: (OnBoardingPageIntent) -> Unit,
) {
    val localDensity = LocalDensity.current
    var topRowWidth by remember { mutableStateOf(0) }
    val topRowWidthDp by remember(topRowWidth) { derivedStateOf { with(localDensity) { topRowWidth.toDp() } } }
    Box(
        Modifier
            .fillMaxSize()
            .paint(
                painterResource(Res.drawable.bg),
                contentScale = ContentScale.FillBounds,
            ),
    ) {
        Card(
            modifier =
                Modifier
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
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.onSizeChanged { topRowWidth = it.width },
                ) {
                    AppImage(
                        model = painterResource(Res.drawable.compose_multiplatform),
                        contentDescription = null,
                        modifier = Modifier.size(AppTheme.dimens.massive),
                        placeholder = Res.drawable.compose_multiplatform,
                    )
                    Column {
                        Text(
                            text = stringResource(MR.strings.onboarding_title),
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = stringResource(MR.strings.app_slogan),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    }
                }
                TextField(
                    value = state.userName,
                    onValueChange = { onIntent(OnBoardingPageIntent.UpdateUserName(it)) },
                    modifier = Modifier.width(topRowWidthDp),
                    label = stringResource(MR.strings.user_name),
                )
                TextField(
                    value = state.accountName,
                    onValueChange = { onIntent(OnBoardingPageIntent.UpdateAccountName(it)) },
                    modifier = Modifier.width(topRowWidthDp),
                    label = stringResource(MR.strings.account_name),
                )
                Row(
                    modifier = Modifier.width(topRowWidthDp),
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium),
                ) {
                    TextField(
                        value = state.accountBalance,
                        onValueChange = { onIntent(OnBoardingPageIntent.UpdateAccountBalance(it)) },
                        modifier = Modifier.weight(1f),
                        label = stringResource(MR.strings.balance),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    )
                    TextField(
                        value = state.accountCurrencyName,
                        onValueChange = { onIntent(OnBoardingPageIntent.UpdateCurrencyName(it)) },
                        modifier = Modifier.weight(1f),
                        label = stringResource(MR.strings.currency),
                    )
                }
                PrimaryButton(
                    onClick = { onIntent(OnBoardingPageIntent.Submit) },
                    modifier =
                        Modifier
                            .padding(AppTheme.dimens.default)
                            .width(topRowWidthDp),
                ) {
                    Text(text = stringResource(MR.strings.submit))
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun OnBoardingScreenPreview() {
    AppTheme {
        OnBoardingPageContent(
            state = OnBoardingPageState(),
            onIntent = {},
        )
    }
}

package com.lightfeather.masarify.page.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.AppDropMenu
import com.lightfeather.designsystem.component.molecules.TextField
import com.lightfeather.designsystem.component.molecules.button.PrimaryButton
import com.lightfeather.designsystem.component.molecules.button.TextButton
import com.lightfeather.designsystem.model.UiCurrency
import com.lightfeather.designsystem.model.getLocalizedName
import com.lightfeather.designsystem.theme.AppTheme
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
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
                verticalArrangement = Arrangement.Center,
            ) {

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = stringResource(MR.strings.onboarding_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = stringResource(MR.strings.app_slogan),
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }

                TextField(
                    value = state.userName,
                    onValueChange = { onIntent(OnBoardingPageIntent.UpdateUserName(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(MR.strings.user_name),
                )
                TextField(
                    value = state.accountName,
                    onValueChange = { onIntent(OnBoardingPageIntent.UpdateAccountName(it)) },
                    modifier = Modifier.fillMaxWidth(),
                    label = stringResource(MR.strings.account_name),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium),
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        Text(
                            text = stringResource(MR.strings.balance),
                            style = MaterialTheme.typography.bodyMedium,
                        )
                        TextField(
                            value = state.accountBalance,
                            modifier = Modifier.height(AppTheme.dimens.component.textField.height.times(1.47f)),
                            onValueChange = { onIntent(OnBoardingPageIntent.UpdateAccountBalance(it)) },
                            label = stringResource(MR.strings.balance),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        )
                    }
                    AppDropMenu(
                        label = stringResource(MR.strings.currency),
                        displayedValue = state.selectedCurrency?.getLocalizedName().orEmpty(),
                        items = state.appCurrencies,
                        modifier = Modifier.height(AppTheme.dimens.component.dropDown.height).weight(1f),
                        containerColor = MaterialTheme.colorScheme.surface,
                        containerShape = MaterialTheme.shapes.medium,
                        labelColor = MaterialTheme.colorScheme.onSurface,
                        onSelectedItem = { onIntent(OnBoardingPageIntent.UpdateCurrency(it)) },
                        contentRow = {
                            Text(
                                text = it.getLocalizedName() + "(${it.symbol})",
                                modifier = Modifier.padding(vertical = AppTheme.dimens.small),
                            )
                        },
                        searchFunction = { currencies, query ->
                            currencies.filter {
                                it.getLocalizedName().contains(query, true) || it.symbol.contains(query, true)
                            }
                        },
                        footerContent = { searchQuery ->
                            TextButton(
                                onClick = {
                                    if (searchQuery.isNotBlank()) {
                                        onIntent(
                                            OnBoardingPageIntent.AddNewCurrency(
                                                UiCurrency("", searchQuery, searchQuery),
                                            ),
                                        )
                                    }
                                },
                                modifier = Modifier.fillMaxWidth(),
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.small),
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Add,
                                        contentDescription = stringResource(MR.strings.add_new_currency_description),
                                    )
                                    Text(stringResource(MR.strings.add_new_currency))
                                }
                            }
                        }
                    )
                }
                PrimaryButton(
                    onClick = { onIntent(OnBoardingPageIntent.Submit) },
                    modifier =
                        Modifier
                            .padding(AppTheme.dimens.default)
                            .fillMaxWidth(),
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

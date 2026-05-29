package tech.lightfeather.masarify.page.paywall

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import tech.lightfeather.designsystem.theme.AppTheme
import tech.lightfeather.domain.repository.PlatformsSlugs
import tech.lightfeather.domain.repository.asSlug
import tech.lightfeather.domain.repository.getPlatform
import tech.lightfeather.masarify.component.SubscribeOnMobileBanner

@Composable
fun PaywallPage(viewModel: PaywallPageViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.onIntent(PaywallPageIntent.LoadStatus)
    }
    PaywallPageContent(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@Composable
internal fun PaywallPageContent(
    state: PaywallPageState,
    onIntent: (PaywallPageIntent) -> Unit,
) {
    val isWeb = getPlatform().asSlug() == PlatformsSlugs.WEB

    if (isWeb) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            SubscribeOnMobileBanner(
                onDismiss = { onIntent(PaywallPageIntent.Dismiss) },
            )
        }
    } else {
        PaywallPageNativeContent(state = state, onIntent = onIntent)
    }
}

@Suppress("LongMethod")
@Composable
private fun PaywallPageNativeContent(
    state: PaywallPageState,
    onIntent: (PaywallPageIntent) -> Unit,
) {
    LazyColumn(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(AppTheme.dimens.spacing.padding.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.medium),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                IconButton(onClick = { onIntent(PaywallPageIntent.Dismiss) }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(MR.strings.close),
                    )
                }
            }
        }

        item {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(AppTheme.dimens.massive),
            )
        }

        item {
            Text(
                text = stringResource(MR.strings.subscription_pro_title),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        item {
            Text(
                text = stringResource(MR.strings.subscription_pro_subtitle),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        item {
            Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.small))
        }

        item {
            ProFeatureItem(stringResource(MR.strings.feature_cloud_sync))
        }

        item {
            ProFeatureItem(stringResource(MR.strings.feature_backup_restore))
        }

        item {
            Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.medium))
        }

        item {
            if (state.isPurchasing || state.isLoading) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { onIntent(PaywallPageIntent.PurchasePro) },
                    modifier = Modifier.fillMaxWidth().testTag("paywall_subscribe_button"),
                ) {
                    Text(stringResource(MR.strings.subscription_subscribe_cta))
                }
            }
        }

        item {
            TextButton(
                onClick = { onIntent(PaywallPageIntent.RestorePurchases) },
                enabled = !state.isPurchasing && !state.isLoading,
            ) {
                Text(stringResource(MR.strings.subscription_restore_purchases))
            }
        }
    }
}

@Composable
private fun ProFeatureItem(text: String) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(vertical = AppTheme.dimens.spacing.padding.tiny),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.spacing.padding.small),
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

package tech.lightfeather.masarify.app

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.button.PrimaryButton
import tech.lightfeather.designsystem.theme.AppTheme
import tech.lightfeather.masarify.auth.BiometricAuthenticator
import dev.icerock.moko.resources.compose.stringResource

@Composable
internal fun BiometricLockScreen(
    authenticator: BiometricAuthenticator,
    onAuthenticated: () -> Unit,
) {
    // Trigger biometric prompt automatically on composition
    LaunchedEffect(Unit) {
        authenticator.authenticate(
            title = "Masarify",
            subtitle = "Authenticate to access your finances",
            onSuccess = onAuthenticated,
            onFailure = { /* User can retry via button */ },
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(AppTheme.dimens.default),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.Fingerprint,
                contentDescription = null,
                modifier = Modifier.size(AppTheme.dimens.massive),
                tint = MaterialTheme.colorScheme.primary,
            )

            Spacer(modifier = Modifier.height(AppTheme.dimens.default))

            Text(
                text = stringResource(MR.strings.biometric_lock_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(AppTheme.dimens.medium))

            Text(
                text = stringResource(MR.strings.biometric_lock_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(AppTheme.dimens.extraLarge))

            PrimaryButton(
                onClick = {
                    authenticator.authenticate(
                        title = "Masarify",
                        subtitle = "Authenticate to access your finances",
                        onSuccess = onAuthenticated,
                        onFailure = { /* Stay on lock screen */ },
                    )
                },
            ) {
                Text(stringResource(MR.strings.biometric_unlock))
            }
        }
    }
}

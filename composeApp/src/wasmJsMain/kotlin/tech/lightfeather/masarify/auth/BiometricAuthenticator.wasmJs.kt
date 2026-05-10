package tech.lightfeather.masarify.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

private object NoOpBiometricAuthenticator : BiometricAuthenticator {
    override fun isAvailable(): Boolean = false

    override fun authenticate(
        title: String,
        subtitle: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    ) {
        onSuccess()
    }
}

@Composable
actual fun rememberBiometricAuthenticator(): BiometricAuthenticator = remember { NoOpBiometricAuthenticator }

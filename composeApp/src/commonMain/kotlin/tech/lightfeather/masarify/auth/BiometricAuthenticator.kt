package tech.lightfeather.masarify.auth

import androidx.compose.runtime.Composable

/**
 * Platform-agnostic biometric authentication interface.
 * Use [rememberBiometricAuthenticator] to obtain a platform-specific instance.
 */
interface BiometricAuthenticator {
    /** Returns true if biometric authentication is available on this device/platform. */
    fun isAvailable(): Boolean

    /**
     * Triggers the biometric authentication prompt.
     *
     * @param title     Dialog title shown to the user.
     * @param subtitle  Dialog subtitle/description shown to the user.
     * @param onSuccess Called when authentication succeeds.
     * @param onFailure Called with an error message when authentication fails or is cancelled.
     */
    fun authenticate(
        title: String,
        subtitle: String,
        cancelText: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit,
    )
}

/**
 * Returns a remembered [BiometricAuthenticator] for the current platform.
 * On platforms where biometrics are unavailable, the returned instance
 * reports [BiometricAuthenticator.isAvailable] == false and calls [onSuccess] immediately.
 */
@Composable
expect fun rememberBiometricAuthenticator(): BiometricAuthenticator

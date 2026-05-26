package tech.lightfeather.masarify.page.auth.resetpassword

internal sealed interface ResetPasswordPageIntent {
    data class UpdateNewPassword(
        val password: String,
    ) : ResetPasswordPageIntent

    data class UpdateConfirmPassword(
        val password: String,
    ) : ResetPasswordPageIntent

    data class Submit(
        val token: String,
    ) : ResetPasswordPageIntent

    data object NavigateToLogin : ResetPasswordPageIntent
}

package tech.lightfeather.masarify.page.auth.forgotpassword

internal sealed interface ForgotPasswordPageIntent {
    data class UpdateEmail(
        val email: String,
    ) : ForgotPasswordPageIntent

    data object Submit : ForgotPasswordPageIntent

    data object NavigateBack : ForgotPasswordPageIntent
}

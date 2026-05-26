package tech.lightfeather.masarify.page.auth.verifyemail

internal sealed interface VerifyEmailPageIntent {
    data class Verify(
        val token: String,
    ) : VerifyEmailPageIntent

    data object GoToDashboard : VerifyEmailPageIntent
}

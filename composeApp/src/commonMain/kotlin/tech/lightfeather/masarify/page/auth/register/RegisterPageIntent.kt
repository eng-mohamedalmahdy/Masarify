package tech.lightfeather.masarify.page.auth.register

sealed interface RegisterPageIntent {
    data class UpdateName(
        val name: String,
    ) : RegisterPageIntent

    data class UpdateEmail(
        val email: String,
    ) : RegisterPageIntent

    data class UpdatePassword(
        val password: String,
    ) : RegisterPageIntent

    data class UpdateConfirmPassword(
        val confirmPassword: String,
    ) : RegisterPageIntent

    data object Submit : RegisterPageIntent

    data object NavigateToLogin : RegisterPageIntent
}

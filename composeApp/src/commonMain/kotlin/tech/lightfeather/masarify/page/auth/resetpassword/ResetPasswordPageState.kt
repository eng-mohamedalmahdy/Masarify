package tech.lightfeather.masarify.page.auth.resetpassword

internal data class ResetPasswordPageState(
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
)

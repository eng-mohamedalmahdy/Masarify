package tech.lightfeather.masarify.page.auth.forgotpassword

internal data class ForgotPasswordPageState(
    val email: String = "",
    val isLoading: Boolean = false,
    val isSent: Boolean = false,
)

package tech.lightfeather.masarify.page.auth.register

data class RegisterPageState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
)

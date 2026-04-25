package com.lightfeather.masarify.page.auth.login

sealed interface LoginPageIntent {
    data class UpdateEmail(
        val email: String,
    ) : LoginPageIntent

    data class UpdatePassword(
        val password: String,
    ) : LoginPageIntent

    data object Submit : LoginPageIntent

    data object NavigateToRegister : LoginPageIntent
}

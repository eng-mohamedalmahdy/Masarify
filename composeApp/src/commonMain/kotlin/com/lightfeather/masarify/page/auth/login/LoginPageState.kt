package com.lightfeather.masarify.page.auth.login

data class LoginPageState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
)

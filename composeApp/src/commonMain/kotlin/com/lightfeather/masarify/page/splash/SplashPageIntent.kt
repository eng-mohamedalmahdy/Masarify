package com.lightfeather.masarify.page.splash

internal sealed interface SplashPageIntent {
    data object NavigateToStart : SplashPageIntent
}

package com.lightfeather.masarify.pages.splash

internal sealed interface SplashPageIntent {
    data object NavigateToStart : SplashPageIntent
}

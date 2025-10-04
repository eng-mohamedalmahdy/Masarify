package com.lightfeather.masarify.page.splash

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SplashPage(viewModel: SplashPageViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    SplashPageContent()
}

@Composable
internal fun SplashPageContent() {
    Text("Splash Page")
}

@Preview
@Composable
fun SplashScreenPreview() {
    AppTheme {
        SplashPageContent()
    }
}


package com.lightfeather.masarify.onboarding

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.lightfeather.designsystem.theme.AppTheme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OnBoardingPage(viewModel: OnBoardingPageViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()

    OnBoardingPageContent(
        state = state,
        onIntent = viewModel::onIntent
    )
}

@Composable
internal fun OnBoardingPageContent(
    state: OnBoardingPageState,
    onIntent: (OnBoardingPageIntent) -> Unit
) {
    // TODO: Stateless UI implementation
}

@Preview
@Composable
fun OnBoardingScreenPreview() {
    AppTheme {
        OnBoardingPageContent(
            state = OnBoardingPageState(),
            onIntent = {}
        )
    }
}

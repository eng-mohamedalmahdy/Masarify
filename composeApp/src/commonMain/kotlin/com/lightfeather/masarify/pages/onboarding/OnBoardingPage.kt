package com.lightfeather.masarify.pages.onboarding

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.lightfeather.designsystem.theme.AppTheme.AppTheme
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import com.lightfeather.masarify.MR
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
    Column {
        Text(stringResource(MR.strings.my_string))
    }
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

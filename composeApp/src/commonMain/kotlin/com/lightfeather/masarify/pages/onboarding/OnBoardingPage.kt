package com.lightfeather.masarify.pages.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lightfeather.designsystem.component.AppImage
import com.lightfeather.designsystem.component.TextField
import com.lightfeather.designsystem.theme.AppTheme
import dev.icerock.moko.resources.compose.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import com.lightfeather.masarify.MR
import masarify.composeapp.generated.resources.Res
import masarify.composeapp.generated.resources.compose_multiplatform
import org.jetbrains.compose.resources.painterResource

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
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AppImage(
                model = painterResource(Res.drawable.compose_multiplatform),
                contentDescription = null,
                modifier = Modifier.size(100.dp),
                placeholder = Res.drawable.compose_multiplatform,

                )
            Column {
                Text(
                    text = stringResource(MR.strings.onboarding_title),
                    style = MaterialTheme.typography.titleLarge,
                )
                Text(
                    text = stringResource(MR.strings.app_slogan),
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
        TextField(
            value = state.userName,
            onValueChange = { onIntent(OnBoardingPageIntent.UpdateUserName(it)) },
            modifier = Modifier.padding(AppTheme.dimens.default),
            label = stringResource(MR.strings.user_name)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun OnBoardingScreenPreview() {
    AppTheme {
        OnBoardingPageContent(
            state = OnBoardingPageState(),
            onIntent = {}
        )
    }
}

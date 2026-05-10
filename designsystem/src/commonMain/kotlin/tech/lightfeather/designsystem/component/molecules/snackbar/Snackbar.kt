package tech.lightfeather.designsystem.component.molecules.snackbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import tech.lightfeather.designsystem.theme.AppTheme

@Composable
fun Snackbar() {
    val snackbarHostState = remember { AppSnackbarHostState() }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .navigationBarsPadding()
                .imePadding(),
    ) {
        SnackbarHost(
            modifier =
                Modifier
                    .align(Alignment.BottomCenter)
                    .padding(
                        bottom = AppTheme.dimens.large,
                        start = AppTheme.dimens.medium,
                        end = AppTheme.dimens.medium,
                    ),
            hostState = snackbarHostState,
        )
    }

    LaunchedEffect(Unit) {
        handleSnackbarMessages(snackbarHostState)
    }
}

@Composable
fun Snackbar(
    message: SnackbarMessage,
    shape: Shape = AppTheme.shapes.medium,
) {
    Snackbar(
        type = message.type,
        leading = null,
        action = null,
        shape = shape,
    ) {
        Text(
            message.textMessage.getText(),
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
fun Snackbar(
    type: SnackbarType,
    shape: Shape = AppTheme.shapes.medium,
    leading: @Composable (RowScope.() -> Unit)? = null,
    action: @Composable (RowScope.() -> Unit)? = null,
    content: @Composable RowScope.() -> Unit,
) {
    CompositionLocalProvider(
        LocalContentColor provides getSnackbarTextColor(type),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(
                        color = getSnackbarBackgroundColor(type),
                        shape = shape,
                    ).padding(AppTheme.dimens.default),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leading != null) {
                leading()
                Spacer(modifier = Modifier.width(AppTheme.dimens.small))
            }

            CompositionLocalProvider(
                LocalTextStyle provides AppTheme.typography.labelSmall,
            ) {
                content()
            }

            if (action != null) {
                Spacer(modifier = Modifier.width(AppTheme.dimens.small))
                action()
            }
        }
    }
}

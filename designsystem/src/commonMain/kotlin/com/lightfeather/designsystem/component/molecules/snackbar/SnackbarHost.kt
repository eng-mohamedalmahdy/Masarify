package com.lightfeather.designsystem.component.molecules.snackbar

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.RecomposeScope
import androidx.compose.runtime.State
import androidx.compose.runtime.currentRecomposeScope
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.AccessibilityManager
import androidx.compose.ui.platform.LocalAccessibilityManager
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.dismiss
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import kotlinx.coroutines.delay

/**
 * Adapted from [androidx.compose.material3.SnackbarHost]
 */
@Composable
fun SnackbarHost(
    hostState: AppSnackbarHostState,
    modifier: Modifier = Modifier,
    snackbar: @Composable (SnackbarMessageData) -> Unit = { snackbarMessageData ->
        Snackbar(
            message = snackbarMessageData.message,
        )
    },
) {
    val currentSnackbarData = hostState.currentSnackbarData
    val accessibilityManager = LocalAccessibilityManager.current

    LaunchedEffect(currentSnackbarData) {
        if (currentSnackbarData != null) {
            val duration =
                currentSnackbarData.message.duration.toMillis(
                    hasAction = false,
                    accessibilityManager,
                )

            if (currentSnackbarData.message.autoDismiss) {
                delay(duration)
                currentSnackbarData.dismiss()
            }
        }
    }

    FadeInFadeOutWithScale(
        current = hostState.currentSnackbarData,
        modifier = modifier,
        content = snackbar,
    )
}

private fun SnackbarDuration.toMillis(
    hasAction: Boolean,
    accessibilityManager: AccessibilityManager?,
): Long {
    val original =
        when (this) {
            SnackbarDuration.Indefinite -> Long.MAX_VALUE
            SnackbarDuration.Long -> LONG_DURATION_MILLIS
            SnackbarDuration.Short -> SHORT_DURATION_MILLIS
        }

    if (accessibilityManager == null) {
        return original
    }

    return accessibilityManager.calculateRecommendedTimeoutMillis(
        original,
        containsIcons = true,
        containsText = true,
        containsControls = hasAction,
    )
}

@Suppress("LongMethod")
@Composable
private fun FadeInFadeOutWithScale(
    current: SnackbarMessageData?,
    modifier: Modifier = Modifier,
    content: @Composable (SnackbarMessageData) -> Unit,
) {
    val state = remember { FadeInFadeOutState<SnackbarMessageData?>() }

    if (current != state.current) {
        state.current = current
        val keys = state.items.map { it.key }.toMutableList()

        if (!keys.contains(current)) {
            keys.add(current)
        }

        state.items.clear()

        keys.filterNotNull().mapTo(state.items) { key ->
            FadeInFadeOutAnimationItem(key) { children ->
                val isVisible = key == current
                val duration = if (isVisible) SNACK_BAR_FADE_IN_MILLIS else SNACK_BAR_FADE_OUT_MILLIS
                val delay = SNACK_BAR_FADE_OUT_MILLIS + SNACK_BAR_IN_BETWEEN_DELAY_MILLIS
                val animationDelay = if (isVisible && keys.filterNotNull().size != 1) delay else 0
                val opacity =
                    animatedOpacity(
                        animation =
                            tween(
                                easing = LinearEasing,
                                delayMillis = animationDelay,
                                durationMillis = duration,
                            ),
                        visible = isVisible,
                        onAnimationFinish = {
                            if (key != state.current) {
                                // leave only the current in the list
                                state.items.removeAll { it.key == key }
                                state.scope?.invalidate()
                            }
                        },
                    )
                val scale =
                    animatedScale(
                        animation =
                            tween(
                                easing = FastOutSlowInEasing,
                                delayMillis = animationDelay,
                                durationMillis = duration,
                            ),
                        visible = isVisible,
                    )
                Box(
                    Modifier
                        .graphicsLayer(
                            scaleX = scale.value,
                            scaleY = scale.value,
                            alpha = opacity.value,
                        ).semantics {
                            liveRegion = LiveRegionMode.Polite
                            dismiss {
                                key.dismiss()
                                true
                            }
                        },
                ) {
                    children()
                }
            }
        }
    }

    Box(modifier) {
        state.scope = currentRecomposeScope
        state.items.forEach { (item, opacity) ->
            key(item) {
                opacity {
                    content(item!!)
                }
            }
        }
    }
}

private class FadeInFadeOutState<T> {
    // we use Any here as something which will not be equals to the real initial value
    var current: Any? = Any()
    val items = mutableListOf<FadeInFadeOutAnimationItem<T>>()
    var scope: RecomposeScope? = null
}

private data class FadeInFadeOutAnimationItem<T>(
    val key: T,
    val transition: FadeInFadeOutTransition,
)

private typealias FadeInFadeOutTransition = @Composable (content: @Composable () -> Unit) -> Unit

@Composable
private fun animatedOpacity(
    animation: AnimationSpec<Float>,
    visible: Boolean,
    onAnimationFinish: () -> Unit = {},
): State<Float> {
    val alphaValue =
        if (!visible) {
            1f
        } else {
            0f
        }

    val alpha = remember { Animatable(alphaValue) }

    LaunchedEffect(visible) {
        val targetValue =
            if (visible) {
                1f
            } else {
                0f
            }

        alpha.animateTo(targetValue, animation)
        onAnimationFinish()
    }
    return alpha.asState()
}

@Composable
private fun animatedScale(
    animation: AnimationSpec<Float>,
    visible: Boolean,
): State<Float> {
    val scaleFactor =
        if (!visible) {
            1f
        } else {
            SNACK_BAR_INVISIBLE_SCALE_FACTOR
        }

    val scale =
        remember {
            Animatable(scaleFactor)
        }

    LaunchedEffect(visible) {
        val targetValue =
            if (visible) {
                1f
            } else {
                SNACK_BAR_INVISIBLE_SCALE_FACTOR
            }

        scale.animateTo(
            targetValue = targetValue,
            animationSpec = animation,
        )
    }

    return scale.asState()
}

private const val SNACK_BAR_FADE_IN_MILLIS = 150
private const val SNACK_BAR_FADE_OUT_MILLIS = 75
private const val SNACK_BAR_INVISIBLE_SCALE_FACTOR = 0.8f
private const val SNACK_BAR_IN_BETWEEN_DELAY_MILLIS = 1

private const val LONG_DURATION_MILLIS = 10000L
private const val SHORT_DURATION_MILLIS = 4000L

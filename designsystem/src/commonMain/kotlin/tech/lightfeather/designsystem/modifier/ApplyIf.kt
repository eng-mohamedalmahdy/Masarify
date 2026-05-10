package tech.lightfeather.designsystem.modifier

import androidx.compose.ui.Modifier

fun Modifier.applyIf(
    condition: Boolean,
    modifier: Modifier,
) = if (condition) {
    this.then(modifier)
} else {
    this
}

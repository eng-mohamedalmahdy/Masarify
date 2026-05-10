package tech.lightfeather.designsystem.component.molecules.button

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import tech.lightfeather.designsystem.theme.AppTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

object SegmentedButton {
    data class Item(
        val text: String,
        val value: String,
    )
}

@Composable
fun AppSegmentedButton(
    items: List<SegmentedButton.Item>,
    selectedValue: String?,
    onSelectionChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    SingleChoiceSegmentedButtonRow(
        modifier = modifier,
    ) {
        items.forEachIndexed { index, item ->
            SegmentedButton(
                shape =
                    SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = items.size,
                    ),
                selected = item.value == selectedValue,
                onClick = { onSelectionChanged(item.value) },
            ) {
                Text(text = item.text)
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSegmentedButton() {
    AppTheme {
        val items =
            listOf(
                SegmentedButton.Item("Day", "day"),
                SegmentedButton.Item("Week", "week"),
                SegmentedButton.Item("Month", "month"),
            )

        AppSegmentedButton(
            items = items,
            selectedValue = "week",
            onSelectionChanged = {},
            modifier = Modifier.padding(AppTheme.dimens.default),
        )
    }
}

@Preview
@Composable
private fun PreviewSegmentedButtonTwoItems() {
    AppTheme {
        val items =
            listOf(
                SegmentedButton.Item("Income", "income"),
                SegmentedButton.Item("Expense", "expense"),
            )

        AppSegmentedButton(
            items = items,
            selectedValue = "income",
            onSelectionChanged = {},
            modifier = Modifier.padding(AppTheme.dimens.default),
        )
    }
}

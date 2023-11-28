package ui.pages.createtransactionpage.view

import ColorWheel
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource
import ext.toHexString
import ui.composeables.CardTextField
import ui.composeables.ImagesList
import ui.entity.UiExpenseCategory
import ui.style.AppTheme

@Composable
fun CreateCategoryDialog(
    images: List<String>,
    onSaveCategoryClick: (UiExpenseCategory) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var categoryName by remember { mutableStateOf("") }
    val categoryColor = remember { mutableStateOf(Color.Transparent) }
    var selectedImageIndex by remember { mutableIntStateOf(0) }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card {
            Column(modifier = Modifier.padding(8.dp)) {
                CardTextField(
                    categoryName,
                    onValueChange = { categoryName = it },
                    label = { Text(stringResource(MR.strings.category_name)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Row() {
                Card(
                    modifier = Modifier.padding(8.dp),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.cardColor)
                ) { ColorWheel(categoryColor, Modifier.size(150.dp).padding(4.dp)) }
                Card(
                    modifier = Modifier.padding(8.dp).height(150.dp),
                    colors = CardDefaults.cardColors(containerColor = AppTheme.cardColor)
                ) {
                    ImagesList(images, selectedImageIndex, categoryColor.value) { selectedImageIndex = it }
                }
            }
            Row {
                TextButton(onDismissRequest, modifier = Modifier.weight(1f)) {
                    Text(stringResource(MR.strings.cancel))
                }
                TextButton({
                    if (images.isNotEmpty()) {
                        onSaveCategoryClick(
                            UiExpenseCategory(
                                categoryName,
                                images[selectedImageIndex],
                                categoryColor.value.toHexString()
                            )
                        )
                    }
                    onDismissRequest()
                }, modifier = Modifier.weight(1f)) {
                    Text(stringResource(MR.strings.save))
                }
            }
        }
    }
}

@Preview
@Composable
private fun CreateCategoryDialogPreview() {
    CreateCategoryDialog(listOf(), {}) {

    }
}
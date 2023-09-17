package ui.pages.createtransactionpage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource
import ui.composeables.LineOnlyTextField
import ui.entity.UiExpenseCategory


@Composable
fun CreateExpensePage() {
    CreateExpensePageViews()
}

@Composable
private fun CreateExpensePageViews() {

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var time by remember { mutableStateOf("") }
    var account by remember { mutableStateOf("") }
    var categories by remember { mutableStateOf(listOf<UiExpenseCategory>()) }

    Column(Modifier.fillMaxSize()) {
        LineOnlyTextField(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            text = title,
            onValueChange = { title = it },
            label = { Text(stringResource(MR.strings.expense)) },
        )
        LineOnlyTextField(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            text = description,
            onValueChange = { description = it },
            label = { Text(stringResource(MR.strings.description)) },
        )
        LineOnlyTextField(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            text = amount,
            onValueChange = { amount = it },
            label = { Text(stringResource(MR.strings.amount)) },
            keyboardType = KeyboardType.Number
        )

    }
}


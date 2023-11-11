package ui.composeables

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource
import ui.entity.UiExpenseCategory
import ui.pages.createtransactionpage.view.CategoryItem

@Composable
fun CategoriesPicker(
    categories: List<UiExpenseCategory>,
    modifier: Modifier = Modifier,
    onCategorySelect: (UiExpenseCategory) -> Unit,
    onAddCategoryClick: () -> Unit
) {

    SearchableGridDropDownMenu(
        categories,
        onValueChange = onCategorySelect,
        label = { Text(stringResource(MR.strings.category_or_tag)) },
        dropDownItem = { CategoryItem(it) },
        modifier = modifier,
        onAddClick = onAddCategoryClick,
        searchFilterFunction = { searchText, item -> item.name.contains(searchText, true) }
    )
}
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import data.dummy.model.DummyDomainModelsProviders
import ui.pages.createtransactionpage.view.CreateCategoryDialog

@Composable
fun TestPage() {
    var isShowingDialog by remember { mutableStateOf(true) }
    if (isShowingDialog) {
        CreateCategoryDialog(List(20) {
            DummyDomainModelsProviders.category.icon
        }, { isShowingDialog = false }) {

        }
    }

}
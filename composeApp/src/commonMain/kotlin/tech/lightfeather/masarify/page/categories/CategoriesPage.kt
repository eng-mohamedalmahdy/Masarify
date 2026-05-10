package tech.lightfeather.masarify.page.categories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation3.runtime.NavKey
import tech.lightfeather.designsystem.MR
import tech.lightfeather.designsystem.component.molecules.EmptyState
import tech.lightfeather.designsystem.component.molecules.button.FloatingActionButton
import tech.lightfeather.designsystem.component.organisms.listitem.CategoryListItem
import tech.lightfeather.designsystem.model.UiCategory
import tech.lightfeather.designsystem.theme.AppTheme
import tech.lightfeather.masarify.navigation.Display
import tech.lightfeather.masarify.navigation.LocalNavigator
import tech.lightfeather.masarify.navigation.Navigator
import tech.lightfeather.masarify.navigation.PreviewNavigator
import tech.lightfeather.masarify.page.categories.addedit.AddEditCategoryPage
import tech.lightfeather.masarify.page.categories.addedit.AddEditCategoryPageViewModel
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.flow.flowOf
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun CategoriesPage(
    viewModel: CategoriesPageViewModel = koinViewModel(),
    navigator: Navigator = LocalNavigator.current,
) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.onIntent(CategoriesPageIntent.LoadData)
    }

    CategoriesPageContent(
        state = state,
        onIntent = viewModel::onIntent,
        navigator = navigator,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
internal fun CategoriesPageContent(
    state: CategoriesPageState,
    onIntent: (CategoriesPageIntent) -> Unit,
    navigator: Navigator,
) {
    // Create scoped list-detail navigator
    val listDetailNav = navigator.forListDetail(CategoriesList)

    // Create list-detail scene strategy for adaptive layout
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    // Collect state flows
    val categories by state.categories.collectAsState(emptyList())

    // Helper to find category by ID
    val findCategory: (String) -> UiCategory? = { categoryId ->
        categories.find { it.id == categoryId }
    }

    listDetailNav.Display(
        sceneStrategy = listDetailStrategy,
        modifier = Modifier,
    ) {
        // List pane entry
        entry<CategoriesList>(
            metadata =
                ListDetailSceneStrategy.listPane(
                    detailPlaceholder = {
                        EmptyState(
                            title = stringResource(MR.strings.no_category_selected_title),
                            message = stringResource(MR.strings.no_category_selected_message),
                            icon = Icons.Outlined.Category,
                            modifier = Modifier.fillMaxSize(),
                        )
                    },
                ),
        ) {
            CategoriesListPane(
                categories = categories,
                selectedCategory = null,
                onAddCategory = {
                    listDetailNav.navigateToDetail(AddEditCategory(categoryId = null))
                },
                onCategoryClick = {
                    listDetailNav.navigateToDetail(AddEditCategory(categoryId = it.id))
                },
                onDeleteCategory = { onIntent(CategoriesPageIntent.DeleteCategory(it)) },
            )
        }

        // Add/Edit category detail pane
        entry<AddEditCategory>(
            metadata = ListDetailSceneStrategy.detailPane(),
        ) { navKey ->
            val category = navKey.categoryId?.let { findCategory(it) } ?: UiCategory.empty
            val vm =
                koinViewModel<AddEditCategoryPageViewModel>(
                    key = "edit_category_${navKey.categoryId}",
                    parameters = {
                        parametersOf(
                            {
                                listDetailNav.back()
                            },
                            category,
                        )
                    },
                )
            AddEditCategoryPane(vm)
        }
    }
}

@Composable
private fun CategoriesListPane(
    categories: List<UiCategory>,
    selectedCategory: UiCategory?,
    onAddCategory: () -> Unit,
    onCategoryClick: (UiCategory) -> Unit,
    onDeleteCategory: (UiCategory) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(AppTheme.dimens.spacing.padding.medium),
            ) {
                Text(
                    text = stringResource(MR.strings.categories),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.small))
                Text(
                    text = stringResource(MR.strings.categories_subtitle),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            // Categories List
            if (categories.isEmpty()) {
                EmptyState(
                    title = stringResource(MR.strings.no_categories_title),
                    message = stringResource(MR.strings.no_categories_message),
                    icon = Icons.Outlined.Category,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(horizontal = AppTheme.dimens.spacing.padding.medium),
                ) {
                    items(
                        items = categories,
                        key = { it.id },
                    ) { category ->
                        CategoryListItem(
                            category = category,
                            isSelected = selectedCategory?.id == category.id,
                            onClick = { onCategoryClick(category) },
                            onDelete = { onDeleteCategory(category) },
                            modifier = Modifier.padding(vertical = AppTheme.dimens.spacing.padding.tiny),
                        )
                    }
                }
            }
        }

        // FAB
        FloatingActionButton(
            onClick = onAddCategory,
            imageVector = Icons.Default.Add,
            contentDescription = stringResource(MR.strings.add_category),
            modifier = Modifier.align(Alignment.BottomEnd).padding(AppTheme.dimens.default),
        )
    }
}

@Composable
private fun AddEditCategoryPane(viewModel: AddEditCategoryPageViewModel) {
    AddEditCategoryPage(viewModel = viewModel)
}

@Preview
@Composable
fun CategoriesPagePreview() {
    AppTheme {
        CategoriesPageContent(
            state =
                CategoriesPageState(
                    categories =
                        flowOf(
                            listOf(
                                UiCategory(
                                    id = "1",
                                    name = "Food",
                                    description = "Food and groceries",
                                    color = "#FF5722",
                                    image = "https://img.icons8.com/ios/50/food.png",
                                ),
                                UiCategory(
                                    id = "2",
                                    name = "Transport",
                                    description = "Transportation expenses",
                                    color = "#2196F3",
                                    image = "https://img.icons8.com/ios/50/car.png",
                                ),
                            ),
                        ),
                ),
            onIntent = {},
            navigator = PreviewNavigator,
        )
    }
}

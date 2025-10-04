package com.lightfeather.masarify.page.categories

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
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.backhandler.BackHandler
import androidx.compose.ui.text.font.FontWeight
import com.lightfeather.designsystem.component.molecules.EmptyState
import com.lightfeather.designsystem.component.molecules.button.FloatingActionButton
import com.lightfeather.designsystem.component.organisms.listitem.CategoryListItem
import com.lightfeather.designsystem.model.UiCategory
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.masarify.MR
import com.lightfeather.masarify.page.categories.addedit.AddEditCategoryPage
import com.lightfeather.masarify.page.categories.addedit.AddEditCategoryPageViewModel
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

internal enum class CategoryNavDestination(
    val id: String?,
) {
    ADD_CATEGORY(null),
    UPDATE_CATEGORY(null),
    ;

    companion object {
        const val ADD_CATEGORY_KEY = "add_category"

        fun updateCategory(categoryId: String) = "update_category_$categoryId"
    }
}

@Composable
fun CategoriesPage(viewModel: CategoriesPageViewModel = koinViewModel()) {
    val state by viewModel.state.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.onIntent(CategoriesPageIntent.LoadData)
    }

    CategoriesPageContent(
        state = state,
        onIntent = viewModel::onIntent,
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalComposeUiApi::class)
@Composable
internal fun CategoriesPageContent(
    state: CategoriesPageState,
    onIntent: (CategoriesPageIntent) -> Unit,
) {
    val navigator = rememberListDetailPaneScaffoldNavigator<String>()
    val coroutineScope = rememberCoroutineScope()
    val categories by state.categories.collectAsState(emptyList())

    BackHandler(navigator.canNavigateBack()) {
        coroutineScope.launch {
            navigator.navigateBack()
            onIntent(CategoriesPageIntent.ClearNavigation)
        }
    }

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            CategoriesListPane(
                categories = categories,
                selectedCategory = state.selectedCategory,
                onAddCategory = {
                    coroutineScope.launch {
                        onIntent(CategoriesPageIntent.NavigationIntent.AddCategory())
                        navigator.navigateTo(
                            androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole.Detail,
                            CategoryNavDestination.ADD_CATEGORY_KEY,
                        )
                    }
                },
                onCategoryClick = {
                    coroutineScope.launch {
                        onIntent(CategoriesPageIntent.NavigationIntent.UpdateCategory(it))
                        navigator.navigateTo(
                            androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole.Detail,
                            CategoryNavDestination.updateCategory(it.id),
                        )
                    }
                },
                onDeleteCategory = { onIntent(CategoriesPageIntent.DeleteCategory(it)) },
            )
        },
        detailPane = {
            val currentDestination = navigator.currentDestination?.contentKey
            Napier.d(
                "Detail pane - currentDestination: $currentDestination, " +
                    "selectedCategory: ${state.selectedCategory?.name}",
                tag = "CategoriesPage",
            )
            when {
                currentDestination == CategoryNavDestination.ADD_CATEGORY_KEY -> {
                    key("add_category") {
                        val vm =
                            koinViewModel<AddEditCategoryPageViewModel>(
                                key = "add_category",
                                parameters = {
                                    parametersOf(
                                        {
                                            coroutineScope.launch {
                                                navigator.navigateBack()
                                                onIntent(CategoriesPageIntent.ClearNavigation)
                                            }
                                        },
                                        UiCategory.empty,
                                    )
                                },
                            )
                        AddEditCategoryPane(vm)
                    }
                }

                currentDestination?.startsWith("update_category_") == true -> {
                    val category = state.selectedCategory
                    if (category != null && category != UiCategory.empty) {
                        Napier.d(
                            "Rendering UpdateCategory - ${category.name} (${category.id})",
                            tag = "CategoriesPage",
                        )
                        key("edit_category_${category.id}") {
                            Napier.d(
                                "Inside key() - form for: ${category.name} (${category.id})",
                                tag = "CategoriesPage",
                            )
                            val vm =
                                koinViewModel<AddEditCategoryPageViewModel>(
                                    key = "edit_category_${category.id}",
                                    parameters = {
                                        parametersOf(
                                            {
                                                coroutineScope.launch {
                                                    navigator.navigateBack()
                                                    onIntent(CategoriesPageIntent.ClearNavigation)
                                                }
                                            },
                                            category,
                                        )
                                    },
                                )
                            AddEditCategoryPane(vm)
                        }
                    } else {
                        EmptyState(
                            title = stringResource(MR.strings.no_category_selected_title),
                            message = stringResource(MR.strings.no_category_selected_message),
                            icon = Icons.Outlined.Category,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }

                else -> {
                    EmptyState(
                        title = stringResource(MR.strings.no_category_selected_title),
                        message = stringResource(MR.strings.no_category_selected_message),
                        icon = Icons.Outlined.Category,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        },
    )
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ThreePaneScaffoldPaneScope.CategoriesListPane(
    categories: List<UiCategory>,
    selectedCategory: UiCategory?,
    onAddCategory: () -> Unit,
    onCategoryClick: (UiCategory) -> Unit,
    onDeleteCategory: (UiCategory) -> Unit,
) {
    AnimatedPane {
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
        )
    }
}

package com.lightfeather.masarify.page.categories

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Category
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.lightfeather.designsystem.component.molecules.AppImage
import com.lightfeather.designsystem.component.molecules.EmptyState
import com.lightfeather.designsystem.component.molecules.button.FloatingActionButton
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.designsystem.util.toColorInt
import com.lightfeather.domain.model.Category
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

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
    val navigator = rememberListDetailPaneScaffoldNavigator<CategoriesPageIntent.NavigationIntent>()
    val coroutineScope = rememberCoroutineScope()
    val categories by state.categories.collectAsState(emptyList())

    BackHandler(navigator.canNavigateBack()) {
        coroutineScope.launch {
            onIntent(CategoriesPageIntent.ClearNavigation(navigator))
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
                        onIntent(CategoriesPageIntent.NavigationIntent.AddCategory(navigator))
                    }
                },
                onCategoryClick = {
                    coroutineScope.launch {
                        onIntent(CategoriesPageIntent.NavigationIntent.UpdateCategory(it, navigator))
                    }
                },
                onDeleteCategory = { onIntent(CategoriesPageIntent.DeleteCategory(it)) },
            )
        },
        detailPane = {
            val currentDestination = navigator.currentDestination?.contentKey
            when (val intent = currentDestination) {
                is CategoriesPageIntent.NavigationIntent.AddCategory -> {
                    key("add_category") {
                        AddEditCategoryPane(
                            category = null,
                            onBack = { onIntent(CategoriesPageIntent.ClearNavigation(navigator)) },
                        )
                    }
                }

                is CategoriesPageIntent.NavigationIntent.UpdateCategory -> {
                    key("edit_category_${intent.category?.id}") {
                        AddEditCategoryPane(
                            category = intent.category,
                            onBack = { onIntent(CategoriesPageIntent.ClearNavigation(navigator)) },
                        )
                    }
                }

                null -> {
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
    categories: List<Category>,
    selectedCategory: Category?,
    onAddCategory: () -> Unit,
    onCategoryClick: (Category) -> Unit,
    onDeleteCategory: (Category) -> Unit,
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
                            CategoryItem(
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
private fun CategoryItem(
    category: Category,
    isSelected: Boolean,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val surfaceColor = MaterialTheme.colorScheme.surface
    val categoryColor = runCatching { Color(category.color.toColorInt()) }.getOrElse { surfaceColor }

    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        shape = AppTheme.shapes.medium,
        colors =
            CardDefaults.cardColors(
                containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = AppTheme.dimens.elevation.level1,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(AppTheme.dimens.spacing.padding.medium),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            // Category Icon with colored background
            Card(
                shape = AppTheme.shapes.small,
                colors =
                    CardDefaults.cardColors(
                        containerColor = categoryColor,
                    ),
                modifier = Modifier.size(AppTheme.dimens.icon.size.large),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    AppImage(
                        model = category.icon,
                        contentDescription = category.name,
                        modifier = Modifier.size(AppTheme.dimens.icon.size.medium),
                    )
                }
            }

            // Category Details
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(horizontal = AppTheme.dimens.spacing.padding.medium),
            ) {
                Text(
                    text = category.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (!category.description.isNullOrBlank()) {
                    Text(
                        text = category.description.orEmpty(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }

            // Delete Button
            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(MR.strings.delete_category),
                    tint = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun AddEditCategoryPane(
    category: Category?,
    onBack: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(AppTheme.dimens.spacing.padding.medium),
    ) {
        Text(
            text =
                if (category == null) {
                    stringResource(MR.strings.add_category)
                } else {
                    stringResource(MR.strings.edit_category)
                },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(AppTheme.dimens.spacing.padding.medium))
        EmptyState(
            title = "Soon",
            message = stringResource(MR.strings.add_edit_category_coming_soon),
            icon = Icons.Outlined.Category,
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview
@Composable
fun CategoriesPagePreview() {
    AppTheme {
        CategoriesPageContent(
            state =
                CategoriesPageState(
                    categories =
                        kotlinx.coroutines.flow.flowOf(
                            listOf(
                                Category(
                                    id = 1,
                                    name = "Food",
                                    description = "Food and groceries",
                                    color = "#FF5722",
                                    icon = "https://img.icons8.com/ios/50/food.png",
                                ),
                                Category(
                                    id = 2,
                                    name = "Transport",
                                    description = "Transportation expenses",
                                    color = "#2196F3",
                                    icon = "https://img.icons8.com/ios/50/car.png",
                                ),
                            ),
                        ),
                ),
            onIntent = {},
        )
    }
}

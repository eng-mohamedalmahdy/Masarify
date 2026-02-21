package com.lightfeather.designsystem.component.organisms

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PieChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.lightfeather.designsystem.MR
import com.lightfeather.designsystem.component.molecules.EmptyState
import com.lightfeather.designsystem.model.UiCategorySpending
import com.lightfeather.designsystem.model.UiSpendingAnalytics
import com.lightfeather.designsystem.model.dummyInstance
import com.lightfeather.designsystem.theme.AppTheme
import com.lightfeather.designsystem.util.stringResource
import com.lightfeather.designsystem.util.toColorInt
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Spending analytics card organism component
 * Displays circular chart with category breakdown and empty state
 *
 * @param spendingAnalytics The spending analytics data (null for empty state)
 * @param isLoading Whether analytics data is loading
 * @param modifier Modifier for the root container
 */
@Composable
fun SpendingAnalyticsCard(
    spendingAnalytics: UiSpendingAnalytics?,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .border(
                    width = AppTheme.dimens.hairline,
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = BORDER_ALPHA),
                    shape = AppTheme.shapes.large,
                ),
        shape = AppTheme.shapes.large,
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            ),
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = AppTheme.dimens.elevation.level1,
            ),
    ) {
        Column(
            modifier = Modifier.padding(AppTheme.dimens.normal),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.normal),
        ) {
            Text(
                text = stringResource(MR.strings.spending_analytics).orEmpty(),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
            )

            when {
                isLoading -> {
                    Box(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(AppTheme.dimens.massive * 3),
                        contentAlignment = Alignment.Center,
                    ) {
                        CircularProgressIndicator()
                    }
                }

                spendingAnalytics == null || spendingAnalytics.categoryBreakdown.isEmpty() -> {
                    EmptyState(
                        title = stringResource(MR.strings.no_analytics_title).orEmpty(),
                        message = stringResource(MR.strings.no_analytics_message).orEmpty(),
                        icon = Icons.Outlined.PieChart,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(AppTheme.dimens.massive * 3),
                    )
                }

                else -> {
                    SpendingAnalyticsContent(spendingAnalytics = spendingAnalytics)
                }
            }
        }
    }
}

@Composable
private fun SpendingAnalyticsContent(
    spendingAnalytics: UiSpendingAnalytics,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.large),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Circular Chart
        Box(
            modifier = Modifier.size(AppTheme.dimens.massive + AppTheme.dimens.huge),
            contentAlignment = Alignment.Center,
        ) {
            CircularSpendingChart(
                categoryBreakdown = spendingAnalytics.categoryBreakdown,
                modifier = Modifier.size(AppTheme.dimens.massive + AppTheme.dimens.huge),
            )

            // Center Label
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Text(
                    text = "$${spendingAnalytics.totalSpending}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        // Category Breakdown List
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(AppTheme.dimens.default),
        ) {
            spendingAnalytics.categoryBreakdown.forEach { categorySpending ->
                CategoryBreakdownItem(categorySpending)
            }
        }
    }
}

@Composable
private fun CircularSpendingChart(
    categoryBreakdown: List<UiCategorySpending>,
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val chartSize = size.minDimension
        val strokeWidth = chartSize * STROKE_WIDTH_RATIO
        val chartRadius = (chartSize - strokeWidth) / 2f
        val center = Offset(size.width / 2f, size.height / 2f)

        var startAngle = -90f

        categoryBreakdown.forEach { category ->
            val sweepAngle = category.percentage * FULL_CIRCLE_DEGREES
            val categoryColor =
                runCatching {
                    Color(category.category.color.toColorInt())
                }.getOrElse { Color.Gray }

            drawArc(
                color = categoryColor,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft =
                    Offset(
                        center.x - chartRadius,
                        center.y - chartRadius,
                    ),
                size = Size(chartRadius * 2f, chartRadius * 2f),
                style =
                    Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Butt,
                    ),
            )

            startAngle += sweepAngle
        }

        // Background circle for remaining percentage
        if (startAngle < FULL_CIRCLE_START_ANGLE) {
            drawArc(
                color = Color.LightGray.copy(alpha = BACKGROUND_ALPHA),
                startAngle = startAngle,
                sweepAngle = FULL_CIRCLE_START_ANGLE - startAngle,
                useCenter = false,
                topLeft =
                    Offset(
                        center.x - chartRadius,
                        center.y - chartRadius,
                    ),
                size = Size(chartRadius * 2f, chartRadius * 2f),
                style =
                    Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Butt,
                    ),
            )
        }
    }
}

@Composable
private fun CategoryBreakdownItem(
    categorySpending: UiCategorySpending,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AppTheme.dimens.medium),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f),
        ) {
            // Category Color Dot
            Box(
                modifier =
                    Modifier
                        .size(AppTheme.dimens.medium)
                        .clip(CircleShape)
                        .background(
                            runCatching {
                                Color(categorySpending.category.color.toColorInt())
                            }.getOrElse { Color.Gray },
                        ),
            )

            Text(
                text = categorySpending.category.name,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }

        Text(
            text = "${(categorySpending.percentage * PERCENTAGE_MULTIPLIER).toInt()}%",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.End,
        )
    }
}

private const val BORDER_ALPHA = 0.3f
private const val STROKE_WIDTH_RATIO = 0.14f
private const val FULL_CIRCLE_DEGREES = 360f
private const val FULL_CIRCLE_START_ANGLE = 270f
private const val BACKGROUND_ALPHA = 0.2f
private const val PERCENTAGE_MULTIPLIER = 100f

@Preview
@Composable
private fun PreviewSpendingAnalyticsCard() {
    AppTheme {
        SpendingAnalyticsCard(
            spendingAnalytics = UiSpendingAnalytics.dummyInstance(),
            isLoading = false,
        )
    }
}

@Preview
@Composable
private fun PreviewSpendingAnalyticsCardEmpty() {
    AppTheme {
        SpendingAnalyticsCard(
            spendingAnalytics = null,
            isLoading = false,
        )
    }
}

@Preview
@Composable
private fun PreviewSpendingAnalyticsCardLoading() {
    AppTheme {
        SpendingAnalyticsCard(
            spendingAnalytics = null,
            isLoading = true,
        )
    }
}

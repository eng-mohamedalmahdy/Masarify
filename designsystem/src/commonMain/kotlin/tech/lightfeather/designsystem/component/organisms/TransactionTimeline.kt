package tech.lightfeather.designsystem.component.organisms

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import tech.lightfeather.designsystem.component.organisms.listitem.StartOverMarkerItem
import tech.lightfeather.designsystem.component.organisms.listitem.TransactionItem
import tech.lightfeather.designsystem.model.TransactionListItem
import tech.lightfeather.designsystem.model.UiAttachment
import tech.lightfeather.designsystem.model.UiFinancialSession
import tech.lightfeather.designsystem.model.UiTransaction
import tech.lightfeather.designsystem.theme.AppTheme

@Composable
fun TransactionTimeline(
    items: List<TransactionListItem>,
    onTransactionClick: (UiTransaction) -> Unit,
    onMarkerToggle: (sessionId: String) -> Unit,
    onMarkerEdit: (UiFinancialSession) -> Unit,
    onMarkerDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
    transactionAttachments: Map<String, List<UiAttachment>> = emptyMap(),
    onTransactionToggle: ((transactionId: String) -> Unit)? = null,
    onTransactionEdit: ((UiTransaction) -> Unit)? = null,
    onTransactionDelete: ((UiTransaction) -> Unit)? = null,
) {
    val lineColor = AppTheme.colors.primary.copy(alpha = 0.25f)

    LazyColumn(modifier = modifier) {
        items(items, key = { item ->
            when (item) {
                is TransactionListItem.TransactionEntry -> "tx_${item.transaction.id}"
                is TransactionListItem.StartOverMarker -> "marker_${item.session.id}"
            }
        }) { item ->
            TimelineRow(lineColor = lineColor) {
                when (item) {
                    is TransactionListItem.TransactionEntry ->
                        TransactionItem(
                            transaction = item.transaction,
                            modifier = Modifier.fillMaxWidth(),
                            isExpanded = item.isExpanded,
                            attachments = transactionAttachments[item.transaction.id] ?: emptyList(),
                            onToggle = onTransactionToggle?.let { { it(item.transaction.id) } },
                            onEdit = onTransactionEdit?.let { { it(item.transaction) } },
                            onDelete = onTransactionDelete?.let { { it(item.transaction) } },
                            onClick = { onTransactionClick(item.transaction) },
                        )

                    is TransactionListItem.StartOverMarker ->
                        StartOverMarkerItem(
                            item = item,
                            onToggle = { onMarkerToggle(item.session.id) },
                            onEdit = onMarkerEdit,
                            onDelete = onMarkerDelete,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(end = AppTheme.dimens.small),
                        )
                }
            }
        }
    }
}

@Composable
private fun TimelineRow(
    lineColor: Color,
    content: @Composable () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
    ) {
        TimelineConnector(color = lineColor)
        Box(modifier = Modifier.weight(1f)) {
            content()
        }
    }
}

@Composable
private fun TimelineConnector(color: Color) {
    val connectorWidth = AppTheme.dimens.large
    val strokeWidthDp = AppTheme.dimens.extraSmall
    val circleRadiusDp = AppTheme.dimens.small
    Box(
        modifier =
            Modifier
                .width(connectorWidth)
                .fillMaxHeight(),
    ) {
        Canvas(modifier = Modifier.matchParentSize()) {
            val x = size.width / 2
            drawLine(
                color = color,
                start = Offset(x, 0f),
                end = Offset(x, size.height),
                strokeWidth = strokeWidthDp.toPx(),
            )
            drawCircle(
                color = color,
                radius = circleRadiusDp.toPx(),
                center = Offset(x, size.height / 2),
            )
        }
    }
}

@Composable
fun TransactionTimelineColumn(
    items: List<TransactionListItem>,
    onTransactionClick: (UiTransaction) -> Unit,
    onMarkerToggle: (sessionId: String) -> Unit,
    onMarkerEdit: (UiFinancialSession) -> Unit,
    onMarkerDelete: (String) -> Unit,
    modifier: Modifier = Modifier,
    transactionAttachments: Map<String, List<UiAttachment>> = emptyMap(),
    onTransactionToggle: ((transactionId: String) -> Unit)? = null,
    onTransactionEdit: ((UiTransaction) -> Unit)? = null,
    onTransactionDelete: ((UiTransaction) -> Unit)? = null,
) {
    val lineColor = AppTheme.colors.primary.copy(alpha = 0.25f)

    Column(modifier = modifier) {
        items.forEach { item ->
            TimelineRow(lineColor = lineColor) {
                when (item) {
                    is TransactionListItem.TransactionEntry ->
                        TransactionItem(
                            transaction = item.transaction,
                            modifier = Modifier.fillMaxWidth(),
                            isExpanded = item.isExpanded,
                            attachments = transactionAttachments[item.transaction.id] ?: emptyList(),
                            onToggle = onTransactionToggle?.let { { it(item.transaction.id) } },
                            onEdit = onTransactionEdit?.let { { it(item.transaction) } },
                            onDelete = onTransactionDelete?.let { { it(item.transaction) } },
                            onClick = { onTransactionClick(item.transaction) },
                        )

                    is TransactionListItem.StartOverMarker ->
                        StartOverMarkerItem(
                            item = item,
                            onToggle = { onMarkerToggle(item.session.id) },
                            onEdit = onMarkerEdit,
                            onDelete = onMarkerDelete,
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .padding(end = AppTheme.dimens.small),
                        )
                }
            }
        }
    }
}

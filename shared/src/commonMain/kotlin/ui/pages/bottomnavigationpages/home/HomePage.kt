package ui.pages.bottomnavigationpages.home

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.SnackbarDuration
import androidx.compose.material.SnackbarHostState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lightfeather.masarify.MR
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import ext.OnUiStateChange
import ext.colorFromHex
import io.github.aakira.napier.Napier
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import ui.pages.bottomnavigationpages.home.model.UiExpenseModel
import ui.style.cardColor


@Composable
fun HomePage() {
    val homePageViewModel = HomePageViewModel(koinInject()).let { getViewModel(Unit, viewModelFactory { it }) }
    val viewModelState = remember { homePageViewModel }
    val expensesListWithDates by viewModelState.expensesWithDateListFlow.collectAsState()

    expensesListWithDates.OnUiStateChange { HomePageViews(it) }
}

@Composable
private fun HomePageViews(expensesList: Map<String, List<UiExpenseModel>>) {

    Napier.d(expensesList.toString(), tag = "ExpensesList")

    AnimatedContent(expensesList) {
        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {

            Spacer(Modifier.height(30.dp))
            Text(
                stringResource(MR.strings.track_income_and_expenses),
                style = MaterialTheme.typography.h4
            )
            if (it.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(Modifier.weight(1f))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(
                            painterResource(MR.images.no_transacions_placeholder),
                            contentDescription = stringResource(MR.strings.no_income_or_expenses_were_added),
                        )
                        Text(
                            stringResource(MR.strings.no_income_or_expenses_were_added),
                            style = MaterialTheme.typography.h5,
                            color = MaterialTheme.colors.onPrimary
                        )
                    }
                    Spacer(Modifier.weight(1f))

                }
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    it.forEach { (date, expenses) ->
                        item {
                            DateItem(date)
                        }
                        items(expenses) {
                            ExpenseCardItem(it)
                        }

                    }

                    item {
                        Spacer(Modifier.height(120.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenseCardItem(expenseModel: UiExpenseModel) {
    val mainCategory = expenseModel.categories.first()
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    Card(
        modifier = Modifier.height(100.dp).padding(8.dp),
        backgroundColor = cardColor,
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Card(
                Modifier.padding(12.dp)
                    .clipToBounds()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        color = mainCategory.color.colorFromHex().copy(alpha = .25f),
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                KamelImage(
                    resource = asyncPainterResource(mainCategory.icon),
                    mainCategory.name,
                    colorFilter = ColorFilter.tint(
                        mainCategory.color.colorFromHex(),
                        blendMode = BlendMode.SrcIn
                    ),
                    modifier = Modifier
                        .size(60.dp)
                        .background(color = mainCategory.color.colorFromHex().copy(alpha = .25f)),
                    onLoading = { progress -> CircularProgressIndicator(progress) },
                    onFailure = { exception ->
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar(
                                message = exception.message.toString(),
                                actionLabel = "Hide",
                                duration = SnackbarDuration.Short
                            )
                        }
                    }
                )
            }
            Column(
                modifier = Modifier.fillMaxHeight().padding(vertical = 16.dp),
            ) {
                Text(expenseModel.title, style = MaterialTheme.typography.h6)
                Spacer(Modifier.weight(1f))
                Text(expenseModel.description, style = MaterialTheme.typography.caption)
            }

            Spacer(Modifier.weight(1f))
            Column(
                modifier = Modifier.fillMaxHeight().padding(vertical = 16.dp).padding(horizontal = 12.dp),
            ) {
                Text(
                    text = "${expenseModel.type.prefixChar} ${expenseModel.amount} ${expenseModel.currencySign}",
                    style = TextStyle(
                        fontSize = 16.sp,
                        color = expenseModel.type.color,
                        fontWeight = FontWeight.Bold
                    )
                )
                Spacer(Modifier.weight(1f))
                Text(
                    text = expenseModel.time,
                    style = MaterialTheme.typography.caption.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                )
            }
        }
    }
}

@Composable
private fun DateItem(date: String) {
    Text(date, modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.h4)
}

@Composable
private fun HomePagePreview() {
    HomePageViews(mapOf())
}
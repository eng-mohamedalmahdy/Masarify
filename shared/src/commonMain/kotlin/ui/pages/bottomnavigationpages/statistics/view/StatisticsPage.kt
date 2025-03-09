package ui.pages.bottomnavigationpages.statistics.view

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerDefaults
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.lightfeather.masarify.MR
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.resources.compose.stringResource
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import ui.composeables.HorizontalIndicator
import ui.entity.UiExpenseCategory
import ui.main.LocalAppTheme
import ui.pages.bottomnavigationpages.home.model.UiTransactionModel
import ui.pages.bottomnavigationpages.statistics.model.CurrencyData
import ui.pages.bottomnavigationpages.statistics.viewmodel.StatisticsViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsPage() {

    val viewModel = StatisticsViewModel(
        koinInject(),
        koinInject(),
        koinInject(),
        koinInject(),
        koinInject(named("expense")),
        koinInject(named("income")),
    ).let {
        getViewModel(Unit, viewModelFactory { it })
    }

    val rememberedViewModel = remember { viewModel }

    val transactionsList by rememberedViewModel.allTransactionsFlow.collectAsState()
    val expenseData by rememberedViewModel.currenciesTotals.collectAsState()
    val expensesCategorised by rememberedViewModel.expensesTotalByCategory.collectAsState()
    val incomeCategorised by rememberedViewModel.incomeTotalByCategory.collectAsState()


    LaunchedEffect(Unit) {
        rememberedViewModel.loadData()
    }


//    val fromDatePickerState = rememberDatePickerState(
//        initialDisplayMode = DisplayMode.Picker,
//        initialDisplayedMonthMillis = 0,
//    )
//    val toDatePickerState = rememberDatePickerState(
//        initialDisplayMode = DisplayMode.Picker,
//        initialDisplayedMonthMillis = Clock.System.now().toEpochMilliseconds(),
//    )


    StatisticsPageViews(
//        fromDatePickerState,
//        toDatePickerState,
        expenseData,
        transactionsList,
        expensesCategorised,
        incomeCategorised
    )
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
private fun StatisticsPageViews(
//    fromDatePickerState: DatePickerState,
//    toDatePickerState: DatePickerState,
    transactionsData: List<CurrencyData>,
    transactions: List<UiTransactionModel>,
    expensesCategorised: List<Pair<UiExpenseCategory, Double>>,
    incomeCategorised: List<Pair<UiExpenseCategory, Double>>,
) {
    val appTheme = LocalAppTheme.current
    var fromDatePicker by remember { mutableStateOf(false) }
    var toDatePicker by remember { mutableStateOf(false) }
    val chartsInsightsPagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) { 3 }


    Box {
        if (fromDatePicker) {
            DatePickerDialog(
                { fromDatePicker = false },
                confirmButton = {
                    Button(
                        onClick = { fromDatePicker = false },
                        Modifier.padding(4.dp)
                    ) {
                        Text(
                            stringResource(MR.strings.confirm),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { fromDatePicker = false },
                        Modifier.padding(4.dp)
                    ) {
                        Text(
                            stringResource(MR.strings.cancel),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            ) {
//                DatePicker(fromDatePickerState)
            }
        }
        if (toDatePicker) {
            DatePickerDialog({ toDatePicker = false },
                confirmButton = {
                    Button(
                        onClick = { toDatePicker = false },
                        Modifier.padding(4.dp)
                    ) {
                        Text(
                            stringResource(MR.strings.confirm),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                dismissButton = {
                    Button(
                        onClick = { toDatePicker = false },
                        Modifier.padding(4.dp)
                    ) {
                        Text(
                            stringResource(MR.strings.cancel),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            ) {
//                DatePicker(toDatePickerState)
            }
        }
        Column(Modifier.fillMaxSize().padding(8.dp).verticalScroll(rememberScrollState())) {

            Text(
                stringResource(MR.strings.statistics),
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineLarge,
                textAlign = TextAlign.Center
            )

//            Row(Modifier.fillMaxWidth()) {
//                CardTextField(
//                    fromDatePickerState.selectedDateMillis?.formatTimeStampToDate() ?: 0L.formatTimeStampToDate(),
//                    onValueChange = {},
//                    label = {
//                        Text(
//                            stringResource(MR.strings.from),
//                            color = MaterialTheme.colorScheme.onBackground,
//                        )
//                    },
//                    readonlyAndDisabled = true,
//                    modifier = Modifier.weight(1f).clickable { fromDatePicker = true },
//                    trailingIcon = Icons.Default.CalendarMonth
//                )
//                Spacer(Modifier.width(4.dp))
//                CardTextField(
//                    toDatePickerState.selectedDateMillis?.formatTimeStampToDate() ?: Clock.System.now()
//                        .toEpochMilliseconds().formatTimeStampToDate(),
//                    onValueChange = {},
//                    label = {
//                        Text(
//                            stringResource(MR.strings.from),
//                            color = MaterialTheme.colorScheme.onBackground,
//                        )
//                    },
//                    readonlyAndDisabled = true,
//                    modifier = Modifier.weight(1f).clickable { toDatePicker = true },
//                    trailingIcon = Icons.Default.CalendarMonth
//                )
//            }

            Spacer(Modifier.height(24.dp))

            Text(
                stringResource(MR.strings.transaction_summary),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))
            TransactionSummaryByCurrency(transactionsData)
            Spacer(Modifier.height(12.dp))

            HorizontalPager(
                chartsInsightsPagerState,
                Modifier.height(450.dp),
                key = { it },
                flingBehavior = PagerDefaults.flingBehavior(
                    state = chartsInsightsPagerState,
                    snapPositionalThreshold = .0f,
                ),
            ) {
                when (it) {
                    0 -> {
                        Card(
                            modifier = Modifier.padding(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = appTheme.cardColor
                            )
                        ) {
                            DailyExpensesLineChart(transactions, modifier = Modifier.padding(8.dp))
                        }
                    }

                    1 -> {
                        Card(
                            modifier = Modifier.padding(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = appTheme.cardColor
                            )
                        ) {
                            CategoriesPieChart(
                                stringResource(MR.strings.expenses),
                                expensesCategorised,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }

                    2 -> {
                        Card(
                            modifier = Modifier.padding(8.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = appTheme.cardColor
                            )
                        ) {
                            CategoriesPieChart(
                                stringResource(MR.strings.income),
                                incomeCategorised,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }
            HorizontalIndicator(chartsInsightsPagerState)
            Spacer(Modifier.height(100.dp))
        }
    }
}




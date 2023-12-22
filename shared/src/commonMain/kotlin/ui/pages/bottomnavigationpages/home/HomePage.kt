package ui.pages.bottomnavigationpages.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissState
import androidx.compose.material3.DismissValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDismissState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lightfeather.masarify.MR
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import ext.OnUiStateChange
import ext.generateBackgroundColor
import ext.navigateSingleTop
import ext.toComposeColor
import ext.truncateIfExceeds
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import org.koin.compose.koinInject
import org.koin.core.qualifier.named
import ui.composeables.SearchTextField
import ui.entity.UiBankAccount
import ui.main.LocalAppTheme
import ui.main.LocalBottomNavigationNavController
import ui.pages.bottomnavigationpages.BottomNavigationPageModel
import ui.pages.bottomnavigationpages.home.model.UiTransactionModel
import ui.pages.createtransactionpage.view.DeleteConfirmationDialog
import ui.util.Preview


@Composable
fun HomePage() {
    val homePageViewModel =
        HomePageViewModel(
            getAllTransactions = koinInject(),
            getAllBankAccounts = koinInject(),
            deleteExpenseUseCase = koinInject(named("expense")),
            deleteIncomeUseCase = koinInject(named("income")),
            deleteTransferUseCase = koinInject(named("transfer"))

        ).let { getViewModel(Unit, viewModelFactory { it }) }
    val viewModelState = remember { homePageViewModel }
    val expensesListWithDates by viewModelState.expensesWithDateListFlow.collectAsState()
    val bankAccounts by viewModelState.bankAccountsListFlow.collectAsState()
    OnUiStateChange(expensesListWithDates) {
        HomePageViews(
            bankAccounts,
            it,
            onSearchTransactions = { viewModelState.filterTransactions(it) },
            onDeleteTransaction = { viewModelState.deleteTransaction(it) })
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HomePageViews(
    bankAccounts: List<UiBankAccount>,
    expensesList: Map<String, List<UiTransactionModel>>,
    onSearchTransactions: (String) -> Unit,
    onDeleteTransaction: (UiTransactionModel) -> Unit
) {
    var searchValue by remember { mutableStateOf("") }
    var toBeDeleted by remember { mutableStateOf<UiTransactionModel?>(null) }
    var toBeEdited by remember { mutableStateOf<UiTransactionModel?>(null) }
    var isShowingDeleteDialog by remember { mutableStateOf(false) }

//
    val router = LocalBottomNavigationNavController.current
    Box(Modifier.fillMaxSize()) {

        if (isShowingDeleteDialog) {
            DeleteConfirmationDialog(
                onDeleteConfirmed = { toBeDeleted?.let { onDeleteTransaction(it) } },
                onDismiss = { isShowingDeleteDialog = false }
            )
        }

        Column(modifier = Modifier.fillMaxSize().padding(8.dp)) {

            Spacer(Modifier.height(8.dp))
            if (expensesList.isNotEmpty()) Row(
                Modifier.padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SearchTextField(
                    searchValue,
                    { searchValue = it; onSearchTransactions(it) },
                    modifier = Modifier.fillMaxWidth(.9f)
                )
//                IconButton(
//                    {},
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .height(56.dp)
//                        .background(LocalAppTheme.current.cardColor, RoundedCornerShape(9.dp))
//                        .clip(RoundedCornerShape(4.dp)),
//
//                    ) {
//                    Image(
//                        Icons.Default.Tune, stringResource(MR.strings.add_new_category_or_tag),
//                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground)
//                    )
//                }
            }
            Spacer(Modifier.height(16.dp))

            Text(
                stringResource(MR.strings.accounts),
                modifier = Modifier.padding(horizontal = 12.dp),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            LazyRow {
                items(bankAccounts) {
                    BankAccountCard(it, onAccountClick = {})
                }
            }
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(MR.strings.your_transactions_history),
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(Modifier.height(10.dp))
            if (expensesList.isEmpty()) {
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
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Spacer(Modifier.weight(1f))

                }
            } else {
                LazyColumn(Modifier.fillMaxSize()) {
                    expensesList.forEach { (date, expenses) ->
                        item {
                            DateItem(date)
                        }
                        items(expenses) { transaction ->
                            val dismissState = rememberDismissState(
                                initialValue = DismissValue.Default,
                                confirmValueChange = {
                                    if (it == DismissValue.DismissedToStart) {
                                        toBeDeleted = transaction
                                        isShowingDeleteDialog = true
                                    } else if (it == DismissValue.DismissedToEnd) {
                                        toBeEdited = transaction
                                        router.navigateSingleTop {
                                            BottomNavigationPageModel.CreateTransactionPageModel(toBeEdited)
                                        }
                                    }
                                    false
                                }
                            )
                            SwipeToDismiss(
                                state = dismissState,
                                background = { DismissExpenseItemBackground(dismissState) },
                                dismissContent = { ExpenseCardItem(transaction) }
                            )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DismissExpenseItemBackground(dismissState: DismissState) {
    val backgroundColor =
        if (dismissState.dismissDirection == DismissDirection.EndToStart) LocalAppTheme.current.deleteColor else LocalAppTheme.current.updateColor
    Card(
        modifier = Modifier.height(100.dp).padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(Modifier.fillMaxSize().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Edit, null, tint = MaterialTheme.colorScheme.onBackground)
            Text(
                stringResource(MR.strings.edit),
                modifier = Modifier.padding(4.dp).padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )
            Spacer(Modifier.weight(1f))
            Text(
                stringResource(MR.strings.delete),
                modifier = Modifier.padding(4.dp).padding(top = 8.dp),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.headlineSmall
            )
            Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.onBackground)
        }
    }
}

@Composable
private fun ExpenseCardItem(expenseModel: UiTransactionModel) {
    val mainCategory = expenseModel.categories.firstOrNull()
    Card(
        modifier = Modifier.height(100.dp).padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = LocalAppTheme.current.cardColor),
        shape = RoundedCornerShape(20.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Card(
                Modifier.padding(12.dp)
                    .clipToBounds()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        color = mainCategory?.color?.toComposeColor()?.copy(alpha = .25f) ?: Color.Black,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                KamelImage(
                    resource = asyncPainterResource(mainCategory?.icon ?: ""),
                    mainCategory?.name,
                    colorFilter = ColorFilter.tint(
                        mainCategory?.color?.toComposeColor()?.generateBackgroundColor(.8f) ?: Color.Black,
                        blendMode = BlendMode.SrcIn
                    ),
                    modifier = Modifier
                        .size(60.dp)
                        .background(color = mainCategory?.color?.toComposeColor() ?: Color.Black)
                        .padding(8.dp),
                    onLoading = { progress -> CircularProgressIndicator(progress) },
                )
            }
            Column(
                modifier = Modifier.fillMaxHeight().padding(vertical = 16.dp),
            ) {
                Text(
                    expenseModel.title.truncateIfExceeds(16),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.weight(1f))
                Text(
                    expenseModel.description,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onBackground
                )
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
                Row {
                    Text(
                        text = expenseModel.time,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}

@Composable
private fun BankAccountCard(bankAccount: UiBankAccount, onAccountClick: (UiBankAccount) -> Unit) {
    Card(
        modifier = Modifier.height(height = 100.dp).clickable { onAccountClick(bankAccount) }
            .padding(vertical = 6.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = bankAccount.color.toComposeColor()),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            Modifier.padding(8.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Row {
                KamelImage(
                    resource = asyncPainterResource(bankAccount.logo),
                    modifier = Modifier.padding(horizontal = 4.dp),
                    contentDescription = bankAccount.color,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onBackground),
                    onFailure = {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            bankAccount.name,
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                )
                Text(
                    text = bankAccount.name,
                    modifier = Modifier.padding(horizontal = 4.dp),
                    color = MaterialTheme.colorScheme.onBackground,
                    style = TextStyle(
                        fontSize = 20.sp,
                        lineHeight = 29.sp,
                        textAlign = TextAlign.Center,
                        letterSpacing = 0.5.sp,
                    ),
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(Modifier.weight(1f))
            Text(
                "${bankAccount.balance} ${bankAccount.currency.sign}",
                modifier = Modifier.padding(horizontal = 4.dp),
                style = TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 29.sp,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp,
                ),
                color = MaterialTheme.colorScheme.onBackground,
                fontWeight = FontWeight.SemiBold

            )
        }
    }
}

@Composable
private fun DateItem(date: String) {
    Text(
        date,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
        style = MaterialTheme.typography.displaySmall,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Preview
@Composable
private fun HomePagePreview() {
    HomePageViews(listOf(), mapOf(), onSearchTransactions = {}, onDeleteTransaction = {})
}
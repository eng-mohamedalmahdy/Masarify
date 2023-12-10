package ui.pages.bottomnavigationpages.currencies

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Update
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.lightfeather.masarify.MR
import dev.icerock.moko.mvvm.compose.getViewModel
import dev.icerock.moko.mvvm.compose.viewModelFactory
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import io.github.aakira.napier.Napier
import org.koin.compose.koinInject
import ui.composeables.CardTextField
import ui.entity.UiCurrency
import ui.main.LocalAppTheme
import ui.pages.bottomnavigationpages.currencies.model.UiExchangeRate


@Composable
fun CurrenciesPage() {
    val viewModel = CurrenciesViewModel(koinInject(), koinInject(), koinInject(), koinInject()).let {
        getViewModel(
            Unit,
            viewModelFactory { it })
    }
    val viewModelState = remember { viewModel }

    val currencies by viewModelState.currenciesFlow.collectAsState()
    val exchangeRates by viewModelState.currenciesExchangeRateFlow.collectAsState()
    var isShowingAddCurrencyDialog by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.loadCurrenciesAndExchangeRates()
        Napier.d(exchangeRates.toString())
    }
    Box {
        if (isShowingAddCurrencyDialog) {
            AddCurrencyDialog(
                onDismissRequest = { isShowingAddCurrencyDialog = false },
                onAddClick = {
                    viewModel.addCurrency(it)
                    isShowingAddCurrencyDialog = false
                })
        }
        AnimatedContent(
            currencies,
            transitionSpec = {
                fadeIn(tween(500)) togetherWith fadeOut(tween(500))
            }
        ) {
            CurrenciesPageViews(it, exchangeRates, onSaveClick = {
                viewModel.saveCurrenciesAndExchangeRates(it)
            }, onAddClick = {
                isShowingAddCurrencyDialog = true
            })
        }
    }

}

@Composable
private fun CurrenciesPageViews(
    currencies: List<UiCurrency>,
    exchangeRates: List<List<UiExchangeRate>>,
    onSaveClick: (List<List<UiExchangeRate>>) -> Unit,
    onAddClick: () -> Unit
) {
    CurrencyTable(currencies = currencies, exchangeRates = exchangeRates, onSaveClick, onAddClick)
}

@Composable
private fun AddCurrencyDialog(onDismissRequest: () -> Unit, onAddClick: (UiCurrency) -> Unit) {
    var currencyName by remember { mutableStateOf("") }
    var currencySymbol by remember { mutableStateOf("") }
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            border = BorderStroke(2.dp, MaterialTheme.colorScheme.secondary)
        ) {
            Column(Modifier.padding(16.dp)) {
                CardTextField(
                    text = currencyName,
                    onValueChange = { currencyName = it },
                    label = { Text(stringResource(MR.strings.currency_name)) }
                )
                CardTextField(
                    text = currencySymbol,
                    onValueChange = { currencySymbol = it },
                    label = { Text(stringResource(MR.strings.currency_symbol)) }
                )
                Button(
                    { onAddClick(UiCurrency(0, currencyName, currencySymbol)) },
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(stringResource(MR.strings.add_currency))
                }
            }
        }
    }
}

@Composable
private fun CurrencyTable(
    currencies: List<UiCurrency>,
    exchangeRates: List<List<UiExchangeRate>>,
    onSaveClick: (List<List<UiExchangeRate>>) -> Unit,
    onAddClick: () -> Unit
) {
    val scrollState = rememberScrollState()
    var inEditMode by remember { mutableStateOf(false) }
    val exchangeRatesState by remember { mutableStateOf(exchangeRates.map { it.toMutableList() }) }

    Column {

//        Text(stringResource(MR.strings.))

        Card(
            Modifier.padding(24.dp).padding(bottom = 100.dp).fillMaxSize(),
            colors = CardDefaults.cardColors(containerColor = LocalAppTheme.current.cardColor),
            shape = RoundedCornerShape(10.dp)
        ) {
            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(10.dp))
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.TopCenter
            ) {

                if (currencies.size > 1) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(rememberScrollState())
                            .padding(bottom = 100.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // Header Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(scrollState)
                        ) {
                            CurrencyCell(text = stringResource(MR.strings.currency), isHeader = true)
                            currencies.forEach { currency ->
                                CurrencyCell(text = currency.name, isHeader = true)
                            }
                        }

                        // Data Rows
                        currencies.forEachIndexed { x, currency ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(scrollState)
                            ) {
                                // Currency Name
                                CurrencyCell(text = currency.name, isHeader = true)

                                // Exchange Rate Cells
                                exchangeRatesState.getOrNull(x)?.forEachIndexed { y, exchangeRate ->
                                    val rate =
                                        if (exchangeRate.fromCurrency == exchangeRate.toCurrency) "-" else "${exchangeRatesState[x][y].rate}"
                                    CurrencyCell(
                                        text = rate,
                                        isEditing = inEditMode && exchangeRate.fromCurrency != exchangeRate.toCurrency,
                                        onTextChange = {
                                            exchangeRatesState[x][y] =
                                                exchangeRate.copy(rate = it.toDoubleOrNull() ?: 0.0)
                                        }
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(MR.images.coin_exchange_rate_placeholder),
                            null,
                            modifier = Modifier.size(200.dp)
                        )
                        Text(
                            stringResource(MR.strings.add_exchange_rate),
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.headlineLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                }

                CustomFloatingActionButton(
                    true,
                    fabIcon = if (inEditMode) Icons.Default.Save else Icons.Default.Update,
                    isSaving = inEditMode,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    onSaveClick = { inEditMode = false; onSaveClick(exchangeRatesState) },
                    onAddClick = onAddClick,
                    onEditClick = { inEditMode = true }
                )
            }
        }
    }
}

// Custom fab that allows for displaying extended content
@Composable
fun CustomFloatingActionButton(
    expandable: Boolean,
    fabIcon: ImageVector,
    isSaving: Boolean,
    onSaveClick: () -> Unit,
    onEditClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isExpanded by remember { mutableStateOf(false) }
    if (!expandable) { // Close the expanded fab if you change to non expandable nav destination
        isExpanded = false
    }



    Column(modifier) {

        // ExpandedBox over the FAB

        AnimatedVisibility(
            isExpanded,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            Column {
                FloatingActionButton(
                    onClick = {
                        onAddClick()
                        isExpanded = false
                    },
                    modifier = Modifier.padding(vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                    )
                }
                FloatingActionButton(
                    onClick = {
                        onEditClick()
                        isExpanded = false
                    },
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .padding(bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                    )
                }
            }
        }


        FloatingActionButton(
            onClick = {
                if (expandable) {
                    if (isSaving) {
                        onSaveClick()
                    } else isExpanded = !isExpanded
                }
            },
            modifier = Modifier,
        ) {
            Crossfade(targetState = isExpanded) { isExpanded ->

                if (isExpanded) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                } else {
                    Icon(
                        imageVector = fabIcon,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun CurrencyCell(
    text: String,
    isEditing: Boolean = false,
    isHeader: Boolean = false,
    onTextChange: (String) -> Unit = {}
) {
    var text by remember { mutableStateOf(text) }
    val cellModifier = if (isHeader) {
        Modifier
            .background(MaterialTheme.colorScheme.primary)
            .border(1.dp, MaterialTheme.colorScheme.onBackground, shape = RectangleShape)
    } else {
        Modifier
            .background(MaterialTheme.colorScheme.background)
            .border(1.dp, MaterialTheme.colorScheme.onBackground, shape = RectangleShape)
    }

    Box(
        modifier = cellModifier.padding(4.dp).size(60.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isEditing) TextField(
            value = text,
            onValueChange = { onTextChange(it);text = it },
            colors = TextFieldDefaults.colors(
                disabledTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedIndicatorColor = Color.Transparent,
                focusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = LocalAppTheme.current.cardColor,
                unfocusedContainerColor = LocalAppTheme.current.cardColor,
                disabledContainerColor = LocalAppTheme.current.cardColor,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
            ),
            shape = RoundedCornerShape(size = 9.dp),
        )
        else Text(
            text,
            color = MaterialTheme.colorScheme.onBackground,
            textAlign = TextAlign.Center,
            style = TextStyle(fontWeight = if (isHeader) FontWeight.Bold else FontWeight.Normal)
        )
    }
}
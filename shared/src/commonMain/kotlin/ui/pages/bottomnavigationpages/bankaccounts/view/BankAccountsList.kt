package ui.pages.bottomnavigationpages.bankaccounts.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FabPosition
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource
import ext.toComposeColor
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import ui.entity.UiBankAccount

@Composable
fun BankAccountsList(
    accounts: List<UiBankAccount>,
    onAccountClick: (UiBankAccount) -> Unit,
    onAddAccountClick: () -> Unit
) {
    var fabHeight by remember { mutableStateOf(0) }

    val heightInDp = with(LocalDensity.current) { fabHeight.toDp() }

    Scaffold(
        Modifier.padding(bottom = 100.dp),
        floatingActionButton = {

            FloatingActionButton(
                modifier = Modifier.onGloballyPositioned {
                    fabHeight = it.size.height
                },
                backgroundColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.background,
                shape = CircleShape,
                onClick = { onAddAccountClick() },
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "icon")
            }
        },
        floatingActionButtonPosition = FabPosition.End
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
                .padding(it),
            contentPadding = PaddingValues(bottom = heightInDp + 16.dp)
        ) {
            item {
                Text(
                    stringResource(MR.strings.my_bank_accounts),
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            items(accounts) {
                BankAccountCard(it, onAccountClick)
            }
        }
    }
}

@Composable
private fun BankAccountCard(bankAccount: UiBankAccount, onAccountClick: (UiBankAccount) -> Unit) {

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onAccountClick(bankAccount) }
            .padding(vertical = 6.dp, horizontal = 16.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = bankAccount.color.toComposeColor()),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Row(Modifier.padding(vertical = 20.dp, horizontal = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            KamelImage(
                resource = asyncPainterResource(bankAccount.logo),
                contentDescription = bankAccount.color,
                onFailure = {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        bankAccount.name,
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            )
            Spacer(Modifier.width(4.dp))
            Text(
                text = bankAccount.name,
                color = MaterialTheme.colorScheme.onBackground,
                style = TextStyle(
                    fontSize = 20.sp,
                    lineHeight = 29.sp,
                    textAlign = TextAlign.Center,
                    letterSpacing = 0.5.sp,
                )
            )
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
                color = MaterialTheme.colorScheme.onBackground
            )
            Icon(
                imageVector = Icons.Default.Menu,
                bankAccount.name,
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
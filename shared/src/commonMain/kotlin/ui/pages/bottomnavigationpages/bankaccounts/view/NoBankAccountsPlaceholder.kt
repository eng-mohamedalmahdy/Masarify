package ui.pages.bottomnavigationpages.bankaccounts.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun NoBankAccountsPlaceholder(onAddClick: () -> Unit) {
    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Spacer(Modifier.height(100.dp))
        Image(painterResource(MR.images.bank), stringResource(MR.strings.you_have_no_bank_accounts))
        Text(
            text = stringResource(MR.strings.you_have_no_bank_accounts),
            style = TextStyle(
                fontSize = 20.sp,
                lineHeight = 29.sp,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp,
            )
        )
        Spacer(Modifier.weight(1f).heightIn(min = 100.dp))


        Text(
            text = stringResource(MR.strings.do_you_want_to_add_new_account),
            style = TextStyle(
                fontSize = 20.sp,
                lineHeight = 29.sp,
                textAlign = TextAlign.Center,
                letterSpacing = 0.5.sp,
            )
        )
        Spacer(Modifier.height(10.dp))
        Button(
            onClick = { onAddClick() },
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
            shape = RoundedCornerShape(9.dp)
        ) {
            Text(stringResource(MR.strings.add_account))
        }
        Spacer(Modifier.height(152.dp))


    }

}
package ui.pages.createtransactionpage.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ext.toComposeColor
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import ui.entity.UiExpenseCategory

@Composable
fun CategoryItem(category: UiExpenseCategory) {

    Column(
        Modifier.size(50.dp, 80.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        KamelImage(
            resource = asyncPainterResource(category.icon),
            category.name,
            colorFilter = ColorFilter.tint(
                category.color.toComposeColor(),
                blendMode = BlendMode.SrcIn
            ),
            modifier = Modifier.size(50.dp),
            onLoading = { progress -> CircularProgressIndicator(progress) },
            onFailure = { Image(Icons.Filled.Warning, it.message) }
        )
        Text(
            category.name,
            style = TextStyle(
                fontSize = 10.sp,
                fontWeight = FontWeight(400),
                color = Color.Black,
                textAlign = TextAlign.Center,
            )
        )
    }
}

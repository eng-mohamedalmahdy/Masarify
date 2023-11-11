package ui.composeables

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource

@Composable
fun ImagesList(list: List<String>, selectedIdx: Int, imageTint: Color = Color.Black, onImageSelected: (Int) -> Unit) {
    LazyVerticalGrid(GridCells.Fixed(3)) {
        itemsIndexed(list) { idx, item ->
            Card(
                modifier = Modifier.size(45.dp).padding(4.dp).clickable { onImageSelected(idx) },
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedIdx == idx) imageTint.copy(alpha = .25f) else MaterialTheme.colorScheme.background
                )
            ) {
                KamelImage(
                    asyncPainterResource(item), null,
                    modifier = Modifier.padding(2.dp),
                    colorFilter = ColorFilter.tint(imageTint)
                )
            }
        }
    }
}
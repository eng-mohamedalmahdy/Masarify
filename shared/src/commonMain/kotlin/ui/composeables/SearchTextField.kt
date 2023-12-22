package ui.composeables

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SearchTextField(
    text: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    CardTextField(
        text,
        onValueChange,
        label = {
            Text(
                stringResource(MR.strings.search),
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        modifier,
        Icons.Outlined.Search,
    )
}
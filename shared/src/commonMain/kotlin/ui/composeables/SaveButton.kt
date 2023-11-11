package ui.composeables

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.lightfeather.masarify.MR
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SaveButton(onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
        shape = RoundedCornerShape(9.dp)
    ) {
        Text(stringResource(MR.strings.save))
        Spacer(Modifier.width(8.dp))
        Icon(Icons.Default.Save, stringResource(MR.strings.save))
    }
}
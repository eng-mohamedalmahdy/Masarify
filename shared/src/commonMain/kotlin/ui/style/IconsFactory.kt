package ui.style

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.graphics.vector.ImageVector

fun getIconByName(name: String): ImageVector =
    when (name) {
        else -> Icons.Filled.Close
    }

package tech.lightfeather.masarify.notification

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

private class AndroidNotificationPermissionRequester(
    private val context: Context,
    private val launcher: ActivityResultLauncher<String>,
    private val pendingCallback: MutableState<((Boolean) -> Unit)?>,
) : NotificationPermissionRequester {

    override fun requestPermission(onResult: (Boolean) -> Unit) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            onResult(true)
            return
        }
        pendingCallback.value = onResult
        launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    override fun isPermissionGranted(): Boolean {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) return true
        return ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS,
        ) == PackageManager.PERMISSION_GRANTED
    }
}

@Composable
actual fun rememberNotificationPermissionRequester(): NotificationPermissionRequester {
    val context = LocalContext.current
    val pendingCallback = remember { mutableStateOf<((Boolean) -> Unit)?>(null) }
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { granted ->
        pendingCallback.value?.invoke(granted)
        pendingCallback.value = null
    }
    return remember(context, launcher) {
        AndroidNotificationPermissionRequester(context, launcher, pendingCallback)
    }
}

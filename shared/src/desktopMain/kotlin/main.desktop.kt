import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.runtime.Composable
import di.getBaseModules
import org.koin.compose.KoinApplication


@Composable
fun MainView() {
    KoinApplication(application = {
        modules(getBaseModules())
    })
    {
        App()
    }
}

@Preview
@Composable
fun AppPreview() {
    App()
}
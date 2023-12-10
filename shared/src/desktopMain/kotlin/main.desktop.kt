import ui.util.Preview
import androidx.compose.runtime.Composable
import di.getBaseModules
import org.koin.compose.KoinApplication
import ui.main.App


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
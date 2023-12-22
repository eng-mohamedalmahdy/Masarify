package ui.pages.webviewpage

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState


@Composable
fun WebViewPage(url: String) {
    Column {
        val state = rememberWebViewState(url)
        state.webSettings.apply {
            isJavaScriptEnabled = true
        }
        Text(text = "${state.pageTitle}")
        val loadingState = state.loadingState
        if (loadingState is LoadingState.Loading) {
            LinearProgressIndicator(
                progress = loadingState.progress,
                modifier = Modifier.fillMaxWidth()
            )
        }
        WebView(
            state,
            modifier = Modifier.fillMaxSize()
        )
    }
}

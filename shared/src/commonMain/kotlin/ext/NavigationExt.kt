package ext

import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.essenty.parcelable.Parcelable
import io.github.xxfast.decompose.router.Router

inline fun <C : Parcelable, reified T : C> Router<C>.navigateSingleTop(crossinline config: () -> T) {
    navigate { stack ->
        val oldConfig: C? = stack.find { it is T }
        if (oldConfig != null) {
            (stack - oldConfig) + oldConfig
        } else {
            stack + config()
        }
    }
}
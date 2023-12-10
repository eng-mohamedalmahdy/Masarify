package ext

import com.arkivanov.decompose.router.stack.navigate
import com.arkivanov.essenty.parcelable.Parcelable
import io.github.xxfast.decompose.router.Router

inline fun <C : Parcelable, reified T : C> Router<C>.navigateSingleTop(
    inclusive: Boolean = false,
    crossinline config: () -> T
) {
    navigate { stack ->
        val oldConfig: C? = stack.find { it is T }
        if (inclusive) {
            if (oldConfig != null) {
                stack
            } else {
                stack + config()
            }
        } else if (oldConfig != null) {
            (stack - oldConfig) + config()
        } else {
            stack + config()
        }
    }
}

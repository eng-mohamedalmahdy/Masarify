package com.lightfeather.masarify.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
abstract class Route : NavKey {
    abstract val routeName: String
}

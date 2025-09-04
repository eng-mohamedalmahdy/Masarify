package com.lightfeather.masarify.navigation

import androidx.core.bundle.Bundle
import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import com.eygraber.uri.Uri
import kotlinx.serialization.json.Json
import kotlin.reflect.KType
import kotlin.reflect.typeOf

object NavTypeProvider {

    inline fun <reified T> provideMapEntry(isNullableAllowed: Boolean = false): Pair<KType, NavType<*>> = typeOf<T>() to provideNavType<T>(isNullableAllowed)

    inline fun <reified T> provideNavType(isNullableAllowed: Boolean ): NavType<T> = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
        override fun put(bundle: SavedState, key: String, value: T) {
            bundle.write {
                putString(key, Json.encodeToString(value))
            }
        }

        override fun get(bundle: SavedState, key: String): T? {
            return bundle.read { getString(key).let { Json.decodeFromString(it) } }
        }

        override fun parseValue(value: String): T {
            return Json.decodeFromString(value)
        }


        override fun serializeAsValue(value: T): String {
            return Uri.parse(Json.encodeToString(value)).toString()
        }


    }

}
package com.lightfeather.masarify.navigation

import androidx.navigation.NavType
import androidx.savedstate.SavedState
import androidx.savedstate.read
import androidx.savedstate.write
import com.eygraber.uri.UriCodec
import kotlinx.serialization.json.Json
import kotlinx.serialization.serializer
import kotlin.reflect.KType
import kotlin.reflect.typeOf

object NavTypeProvider {
    inline fun <reified T> provideMapEntry(isNullableAllowed: Boolean = false): Pair<KType, NavType<*>> =
        typeOf<T>() to provideNavType<T>(isNullableAllowed)

    inline fun <reified T> provideNavType(isNullableAllowed: Boolean): NavType<T> =
        object : NavType<T>(isNullableAllowed = isNullableAllowed) {
            override fun put(
                bundle: SavedState,
                key: String,
                value: T,
            ) {
                bundle.write {
                    putString(key, Json.encodeToString(serializer<T>(), value))
                }
            }

            override fun get(
                bundle: SavedState,
                key: String,
            ): T? = bundle.read { getString(key)?.let { Json.decodeFromString(serializer<T>(), it) } }

            override fun parseValue(value: String): T = Json.decodeFromString(serializer<T>(), UriCodec.decode(value))

            override fun serializeAsValue(value: T): String =
                UriCodec.encode(Json.encodeToString(serializer<T>(), value))
        }
}

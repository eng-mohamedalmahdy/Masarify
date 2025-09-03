package com.lightfeather.happytail.navigation

import androidx.core.bundle.Bundle
import androidx.navigation.NavType
import com.eygraber.uri.Uri
import kotlinx.serialization.json.Json
import kotlin.reflect.KType
import kotlin.reflect.typeOf

object NavTypeProvider {

    inline fun <reified T> provideMapEntry(isNullableAllowed: Boolean = false): Pair<KType, NavType<*>> = typeOf<T>() to provideNavType<T>(isNullableAllowed)

    inline fun <reified T> provideNavType(isNullableAllowed: Boolean ): NavType<T> = object : NavType<T>(isNullableAllowed = isNullableAllowed) {
        override fun get(bundle: Bundle, key: String): T? {
            return bundle.getString(key)?.let { Json.decodeFromString(it) }
        }

        override fun parseValue(value: String): T {
            return Json.decodeFromString(value)
        }

        override fun put(bundle: Bundle, key: String, value: T) {
            bundle.putString(key, Json.encodeToString(value))
        }

        override fun serializeAsValue(value: T): String {
            return Uri.parse(Json.encodeToString(value)).toString()
        }


    }

}
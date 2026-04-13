package com.lightfeather.data.di

import com.lightfeather.data.local.AppPreferences
import com.lightfeather.data.local.database.drivers.SharedDatabase
import com.lightfeather.data.remote.RemoteRatesApi
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

/** Base URL for the Masarify backend. Update this to match your server address. */
const val MASARIFY_BACKEND_BASE_URL = "http://192.168.1.7:8080"

val dataModule =
    module {
        single<SharedDatabase> { SharedDatabase(get()) }
        single<AppPreferences> { AppPreferences(get()) }
        single<Settings> { Settings() }
        single<HttpClient> {
            HttpClient {
                install(ContentNegotiation) {
                    json(
                        Json {
                            ignoreUnknownKeys = true
                            isLenient = true
                            prettyPrint = true
                        },
                    )
                }
            }
        }
        single<RemoteRatesApi> { RemoteRatesApi(get(), MASARIFY_BACKEND_BASE_URL) }
    }

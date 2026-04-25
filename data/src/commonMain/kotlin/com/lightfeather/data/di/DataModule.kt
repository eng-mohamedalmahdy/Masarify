package com.lightfeather.data.di

import com.lightfeather.data.local.AppPreferences
import com.lightfeather.data.local.database.drivers.SharedDatabase
import com.lightfeather.data.remote.AuthApi
import com.lightfeather.data.remote.RemoteRatesApi
import com.lightfeather.data.remote.SyncApi
import com.russhwolf.settings.Settings
import io.ktor.client.HttpClient
import io.ktor.client.plugins.auth.Auth
import io.ktor.client.plugins.auth.providers.BearerTokens
import io.ktor.client.plugins.auth.providers.bearer
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.dsl.module

/** Base URL for the Masarify backend. Update this to match your server address. */
const val MASARIFY_BACKEND_BASE_URL = "http://192.168.1.7:8080/api"

val dataModule =
    module {
        single<SharedDatabase> { SharedDatabase(get()) }
        single<AppPreferences> { AppPreferences(get()) }
        single<Settings> { Settings() }
        single<HttpClient> {
            val preferences: AppPreferences = get()
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
                install(Auth) {
                    bearer {
                        loadTokens {
                            val access = preferences.accessToken ?: return@loadTokens null
                            val refresh = preferences.refreshToken ?: return@loadTokens null
                            BearerTokens(access, refresh)
                        }
                        refreshTokens {
                            val refreshToken = preferences.refreshToken ?: return@refreshTokens null
                            val authApi = AuthApi(client, MASARIFY_BACKEND_BASE_URL)
                            authApi.refresh(refreshToken).getOrNull()?.let { tokens ->
                                preferences.accessToken = tokens.accessToken
                                preferences.refreshToken = tokens.refreshToken
                                BearerTokens(tokens.accessToken, tokens.refreshToken)
                            }
                        }
                    }
                }
            }
        }
        single<RemoteRatesApi> { RemoteRatesApi(get(), MASARIFY_BACKEND_BASE_URL) }
        single<AuthApi> { AuthApi(get(), MASARIFY_BACKEND_BASE_URL) }
        single<SyncApi> { SyncApi(get(), MASARIFY_BACKEND_BASE_URL) }
    }

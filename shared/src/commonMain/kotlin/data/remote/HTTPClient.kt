package data.remote

import io.ktor.client.HttpClient
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.CacheControl
import io.ktor.serialization.kotlinx.json.json

val httpClient = HttpClient {
    install(ContentNegotiation){
        json()
    }
    install(HttpCache)
}
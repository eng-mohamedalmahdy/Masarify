package data.remote

import data.remote.model.HTTPRequestResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

suspend fun getImagesIcons(httpClient: HttpClient): HTTPRequestResponse<List<String>> {
    return httpClient.get("https://drive.google.com/uc?id=1THFcgO3CAnVZo2tIPkRngdcVGDw7rMjr")
        .body<HTTPRequestResponse<List<String>>>()
}
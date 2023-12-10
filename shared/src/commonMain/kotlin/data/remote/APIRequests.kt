package data.remote

import data.remote.model.HTTPRequestResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

suspend fun getImagesIcons(httpClient: HttpClient): HTTPRequestResponse<List<String>> {
    return try {
        httpClient.get("https://api.npoint.io/53feb761d1a0906371ad")
            .body<HTTPRequestResponse<List<String>>>()
    } catch (e: Exception) {
        HTTPRequestResponse(listOf())
    }

}
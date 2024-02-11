package data.remote

import data.remote.model.HTTPRequestResponse
import io.github.aakira.napier.Napier
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

suspend fun getImagesIcons(httpClient: HttpClient): HTTPRequestResponse<List<String>> {
    return try {
        httpClient.get("https://masarify.github.io/masarify_icons/icons.json")
            .body<HTTPRequestResponse<List<String>>>()
    } catch (e: Exception) {
        Napier.d(e.message.toString(), tag = "ICONS")
        val dataList = listOf(
            "https://img.icons8.com/ios/50/bus.png",
            "https://img.icons8.com/ios/50/ingredients.png",
            "https://img.icons8.com/pastel-glyph/64/junk-food--v2.png",
            "https://img.icons8.com/pastel-glyph/64/shopping-cart--v1.png",
            "https://img.icons8.com/wired/64/meal.png",
            "https://img.icons8.com/ios/50/light-on--v1.png",
            "https://img.icons8.com/sf-black/64/home-page.png",
            "https://img.icons8.com/ios-filled/50/car.png",
            "https://img.icons8.com/ios-filled/50/pill.png",
            "https://img.icons8.com/fluency-systems-filled/48/clapperboard-2.png",
            "https://img.icons8.com/ios-glyphs/30/airplane-mode-on.png",
            "https://img.icons8.com/ios/50/book--v1.png",
            "https://img.icons8.com/glyph-neue/64/education.png",
            "https://img.icons8.com/windows/32/gift.png",
            "https://img.icons8.com/material-rounded/24/security-checked.png",
            "https://img.icons8.com/ios/50/maintenance--v1.png",
            "https://img.icons8.com/ios-filled/50/apple-phone.png",
            "https://img.icons8.com/external-bearicons-detailed-outline-bearicons/64/external-Subscribe-social-media-bearicons-detailed-outline-bearicons.png",
            "https://img.icons8.com/ios-filled/50/bounced-check.png",
            "https://img.icons8.com/wired/64/apple-stocks.png",
            "https://img.icons8.com/ios-filled/50/family--v1.png",
            "https://img.icons8.com/ios-filled/50/football.png",
            "https://img.icons8.com/ios-filled/50/pin--v1.png",
            "https://img.icons8.com/ios-filled/50/two-tickets.png",
            "https://img.icons8.com/ios-filled/50/wifi-logo.png",
            "https://img.icons8.com/ios-filled/50/bread.png",
            "https://img.icons8.com/ios/50/camping-tent.png",
            "https://img.icons8.com/ios-filled/50/earth-planet.png",
            "https://img.icons8.com/sf-black-filled/64/jewelry.png",
            "https://img.icons8.com/ios-filled/50/ps-controller.png",
            "https://img.icons8.com/ios-glyphs/30/cup.png",
            "https://img.icons8.com/external-anggara-outline-color-anggara-putra/32/external-thermometer-medical-and-healthcare-anggara-outline-color-anggara-putra.png",
            "https://img.icons8.com/external-kosonicon-solid-kosonicon/48/external-dumbbell-sports-equipment-kosonicon-solid-kosonicon.png",
            "https://img.icons8.com/ios-filled/50/cat-footprint.png"
        )

        HTTPRequestResponse(dataList)
    }

}
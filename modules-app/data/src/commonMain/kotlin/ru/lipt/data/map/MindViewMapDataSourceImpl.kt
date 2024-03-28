package ru.lipt.data.map

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import ru.lipt.core.device.ApplicationConfig
import ru.lipt.domain.map.MindViewMapDataSource
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote

class MindViewMapDataSourceImpl(
    private val config: ApplicationConfig,
    private val client: HttpClient,
) : MindViewMapDataSource {

    // :TODO replace pair to normal domain-data class
    override suspend fun fetch(request: Pair<String, String>): SummaryViewMapResponseRemote =
        client.get(
            urlString = "${config.baseUrl}/maps/view?mapId=${request.first}&userId=${request.second}"
        ).body()
}

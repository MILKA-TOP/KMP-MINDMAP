package ru.lipt.data.catalog

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import ru.lipt.core.device.ApplicationConfig
import ru.lipt.domain.catalog.CatalogDataSource
import ru.lipt.domain.catalog.models.CatalogMindMap
import ru.lipt.domain.catalog.models.MindMapQueryResponse

class CatalogDataSourceImpl(
    private val client: HttpClient,
    private val config: ApplicationConfig,
) : CatalogDataSource {
    override suspend fun createMap(title: String, description: String, password: String?): CatalogMindMap =
        TODO()

    override suspend fun search(query: String): List<MindMapQueryResponse> = listOf(
        MindMapQueryResponse(
            id = "0",
            title = "Base title 1",
            description = "Description 1",
            isNeedPassword = true
        ), MindMapQueryResponse(
            id = "1",
            title = "Base title 2",
            description = "Description 2",
            isNeedPassword = false
        )
    )

    override suspend fun addPublicMap(mapId: String) = Unit
    override suspend fun addPrivateMap(mapId: String, password: String) = Unit

    override suspend fun fetch(request: Unit): List<CatalogMindMap> =
        client.get(
            urlString = "${config.baseUrl}/catalog"
        ).body()
}

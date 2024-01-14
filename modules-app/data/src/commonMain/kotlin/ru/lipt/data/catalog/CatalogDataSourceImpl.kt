package ru.lipt.data.catalog

import ru.lipt.domain.catalog.CatalogDataSource
import ru.lipt.domain.catalog.models.CatalogMindMap
import ru.lipt.domain.catalog.models.MindMapQueryResponse

class CatalogDataSourceImpl : CatalogDataSource {
    override suspend fun createMap(title: String, description: String, password: String?): CatalogMindMap =
        CatalogMindMap(id = "newId", title = title, description = description)

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

    override suspend fun fetch(request: Unit): List<CatalogMindMap> {
        return listOf(
            CatalogMindMap(
                id = "0",
                title = "First data source title",
                description = "First data source description"
            ),
            CatalogMindMap(
                id = "1",
                title = "Second data source title",
                description = "Second data source description"
            ),
            CatalogMindMap(
                id = "2",
                title = "Third data source title",
                description = "Third data source description"
            )
        )
    }
}

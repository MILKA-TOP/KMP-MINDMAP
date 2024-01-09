package ru.lipt.data.catalog

import ru.lipt.domain.catalog.CatalogDataSource
import ru.lipt.domain.catalog.models.CatalogMindMap

class CatalogDataSourceImpl : CatalogDataSource {
    override suspend fun createMap(title: String, description: String, password: String?): CatalogMindMap =
        CatalogMindMap(id = "newId", title = title, description = description)

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

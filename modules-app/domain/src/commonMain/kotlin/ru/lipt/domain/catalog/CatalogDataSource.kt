package ru.lipt.domain.catalog

import ru.lipt.core.cache.RemoteDataSource
import ru.lipt.domain.catalog.models.CatalogMindMap
import ru.lipt.domain.catalog.models.MindMapQueryResponse

interface CatalogDataSource : RemoteDataSource<Unit, List<CatalogMindMap>> {
    suspend fun createMap(title: String, description: String, password: String? = null): CatalogMindMap
    suspend fun search(query: String): List<MindMapQueryResponse>
    suspend fun addPublicMap(mapId: String)
    suspend fun addPrivateMap(mapId: String, password: String)
}

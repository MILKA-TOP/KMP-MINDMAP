package ru.lipt.domain.catalog

import ru.lipt.core.cache.RemoteDataSource
import ru.lipt.domain.catalog.models.CatalogMindMap

interface CatalogDataSource : RemoteDataSource<Unit, List<CatalogMindMap>> {
    suspend fun createMap(title: String, description: String, password: String? = null): CatalogMindMap
    suspend fun search(query: String): List<CatalogMindMap>
    suspend fun addMap(mapId: String, password: String?)
    suspend fun addPrivateMap(mapId: String, password: String)
}

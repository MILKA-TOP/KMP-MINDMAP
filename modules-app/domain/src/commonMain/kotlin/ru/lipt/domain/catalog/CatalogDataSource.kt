package ru.lipt.domain.catalog

import ru.lipt.core.cache.RemoteDataSource
import ru.lipt.domain.catalog.models.CatalogMindMap

interface CatalogDataSource : RemoteDataSource<Unit, List<CatalogMindMap>> {
    suspend fun createMap(title: String, description: String, password: String? = null): CatalogMindMap
}

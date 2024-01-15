package ru.lipt.domain.catalog

import ru.lipt.core.cache.CachePolicyRepository
import ru.lipt.domain.catalog.models.CatalogMindMap
import ru.lipt.domain.catalog.models.MindMapQueryResponse

class CatalogRepository(
    localDataSource: CatalogLocalDataSource,
    private val remoteDataSource: CatalogDataSource
) : CachePolicyRepository<Unit, List<CatalogMindMap>>(
    localDataSource = localDataSource,
    remoteDataSource = remoteDataSource,
) {
    suspend fun createMap(title: String, description: String, password: String? = null): CatalogMindMap =
        remoteDataSource.createMap(title, description, password)
            .also { map -> updateCache(Unit) { this + map } }

    suspend fun search(query: String): List<MindMapQueryResponse> = remoteDataSource.search(query)

    suspend fun addPublicMap(mapId: String) = remoteDataSource.addPublicMap(mapId)
    suspend fun addPrivateMap(mapId: String, password: String) = remoteDataSource.addPrivateMap(mapId, password)
    suspend fun removeMap(mapId: String) = updateCache(Unit) {
        this.filter { it.id != mapId }
    }
}

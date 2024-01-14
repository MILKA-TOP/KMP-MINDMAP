package ru.lipt.domain.catalog

import ru.lipt.core.cache.CachePolicy
import ru.lipt.domain.catalog.models.CatalogMindMap
import ru.lipt.domain.catalog.models.MindMapQueryResponse

class CatalogInteractor(
    private val catalogRepository: CatalogRepository,
) {

    suspend fun getMaps(): List<CatalogMindMap> = catalogRepository.fetch(Unit, CachePolicy.ALWAYS).orEmpty()

    suspend fun createMap(title: String, description: String, password: String? = null): CatalogMindMap =
        catalogRepository.createMap(title, description, password)

    suspend fun search(query: String): List<MindMapQueryResponse> = catalogRepository.search(query)

    suspend fun addPublicMap(mapId: String) = catalogRepository.addPublicMap(mapId)
    suspend fun addPrivateMap(mapId: String, password: String) = catalogRepository.addPrivateMap(mapId, password)
}

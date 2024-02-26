package ru.lipt.domain.catalog

import ru.lipt.core.cache.CachePolicy
import ru.lipt.domain.catalog.models.CatalogMindMap

class CatalogInteractor(
    private val catalogRepository: CatalogRepository,
) {

    suspend fun getMaps(): List<CatalogMindMap> = catalogRepository.fetch(Unit, CachePolicy.ALWAYS).orEmpty()
    suspend fun fetchMaps(): List<CatalogMindMap> = catalogRepository.fetch(Unit, CachePolicy.REFRESH).orEmpty()

    suspend fun createMap(
        title: String,
        description: String,
        password: String? = null,
        mapRefId: String? = null
    ): String = catalogRepository.createMap(title, description, password, mapRefId)

    suspend fun search(query: String): List<CatalogMindMap> = catalogRepository.search(query)

    suspend fun addMap(mapId: String, password: String? = null) =
        catalogRepository.addMap(mapId, password).also {
            runCatching { fetchMaps() }
        }
}

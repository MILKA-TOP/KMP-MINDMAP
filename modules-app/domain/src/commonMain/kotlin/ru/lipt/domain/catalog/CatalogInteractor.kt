package ru.lipt.domain.catalog

import ru.lipt.core.cache.CachePolicy
import ru.lipt.domain.catalog.models.CatalogMindMap

class CatalogInteractor(
    private val catalogRepository: CatalogRepository,
) : ICatalogInteractor {
    override suspend fun getMaps(): List<CatalogMindMap> = catalogRepository.fetch(Unit, CachePolicy.ALWAYS).orEmpty()
    override suspend fun fetchMaps(): List<CatalogMindMap> = catalogRepository.fetch(Unit, CachePolicy.REFRESH).orEmpty()

    override suspend fun createMap(
        title: String,
        description: String,
        password: String?,
        mapRefId: String?
    ): String = catalogRepository.createMap(title, description, password, mapRefId)

    override suspend fun migrate(text: String, password: String?, type: MigrateType): String =
        catalogRepository.migrate(text, password, type)

    override suspend fun search(query: String): List<CatalogMindMap> = catalogRepository.search(query)

    override suspend fun addMap(mapId: String, password: String?) =
        catalogRepository.addMap(mapId, password).also {
            runCatching { fetchMaps() }
        }
}

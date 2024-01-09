package ru.lipt.domain.catalog

import ru.lipt.core.cache.CachePolicy
import ru.lipt.domain.catalog.models.CatalogMindMap

class CatalogInteractor(
    private val catalogRepository: CatalogRepository,
) {

    suspend fun getMaps(): List<CatalogMindMap> = catalogRepository.fetch(Unit, CachePolicy.ALWAYS).orEmpty()

    suspend fun createMap(title: String, description: String, password: String? = null): CatalogMindMap =
        catalogRepository.createMap(title, description, password)
}

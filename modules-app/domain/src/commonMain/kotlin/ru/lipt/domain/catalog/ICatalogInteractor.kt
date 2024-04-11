package ru.lipt.domain.catalog

import ru.lipt.domain.catalog.models.CatalogMindMap

interface ICatalogInteractor {
    suspend fun getMaps(): List<CatalogMindMap>
    suspend fun fetchMaps(): List<CatalogMindMap>

    suspend fun createMap(
        title: String,
        description: String,
        password: String? = null,
        mapRefId: String? = null
    ): String

    suspend fun migrate(text: String, password: String? = null, type: MigrateType = MigrateType.MINDOMO_TEXT): String

    suspend fun search(query: String): List<CatalogMindMap>

    suspend fun addMap(mapId: String, password: String? = null)
}

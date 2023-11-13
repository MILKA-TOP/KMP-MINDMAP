package ru.lipt.domain.catalog

import ru.lipt.core.cache.CachePolicyRepository
import ru.lipt.domain.catalog.models.CatalogMindMap

class CatalogRepository(
    localDataSource: CatalogLocalDataSource,
    remoteDataSource: CatalogDataSource
) : CachePolicyRepository<Unit, List<CatalogMindMap>>(
    localDataSource = localDataSource,
    remoteDataSource = remoteDataSource,
)

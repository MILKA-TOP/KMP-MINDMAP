package ru.lipt.domain.catalog

import ru.lipt.core.cache.RemoteDataSource
import ru.lipt.domain.catalog.models.CatalogMindMap

interface CatalogDataSource : RemoteDataSource<Unit, List<CatalogMindMap>>

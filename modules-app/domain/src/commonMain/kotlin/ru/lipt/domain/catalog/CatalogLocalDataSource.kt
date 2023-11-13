package ru.lipt.domain.catalog

import ru.lipt.core.cache.InMemoryLocalDataSource
import ru.lipt.domain.catalog.models.CatalogMindMap

class CatalogLocalDataSource : InMemoryLocalDataSource<Unit, List<CatalogMindMap>>()

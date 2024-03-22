package ru.lipt.domain.map

import ru.lipt.core.cache.InMemoryLocalDataSource
import ru.lipt.domain.map.models.update.MapsUpdateRequestParams

class MindMapUpdateLocalDataSource : InMemoryLocalDataSource<String, MapsUpdateRequestParams>()

package ru.lipt.domain.map

import ru.lipt.core.cache.InMemoryLocalDataSource
import ru.lipt.domain.map.models.MindMap

class MindMapLocalDataSource : InMemoryLocalDataSource<String, MindMap>()

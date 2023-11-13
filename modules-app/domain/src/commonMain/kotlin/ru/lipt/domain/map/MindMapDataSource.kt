package ru.lipt.domain.map

import ru.lipt.core.cache.RemoteDataSource
import ru.lipt.domain.map.models.MindMap

interface MindMapDataSource : RemoteDataSource<String, MindMap>

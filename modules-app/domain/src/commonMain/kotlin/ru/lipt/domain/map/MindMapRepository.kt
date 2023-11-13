package ru.lipt.domain.map

import ru.lipt.core.cache.CachePolicyRepository
import ru.lipt.domain.map.models.MindMap

class MindMapRepository(
    localDataSource: MindMapLocalDataSource,
    remoteDataSource: MindMapDataSource
) : CachePolicyRepository<String, MindMap>(
    localDataSource = localDataSource,
    remoteDataSource = remoteDataSource,
)

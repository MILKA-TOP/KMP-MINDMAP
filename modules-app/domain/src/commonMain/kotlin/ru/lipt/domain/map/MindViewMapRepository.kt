package ru.lipt.domain.map

import ru.lipt.core.cache.CachePolicyRepository
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote

class MindViewMapRepository(
    localDataSource: MindViewMapLocalDataSource,
    remoteDataSource: MindViewMapDataSource
) : CachePolicyRepository<Pair<String, String>, SummaryViewMapResponseRemote>(
    localDataSource = localDataSource,
    remoteDataSource = remoteDataSource,
)

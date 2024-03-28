package ru.lipt.domain.map

import ru.lipt.core.cache.RemoteDataSource
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote

interface MindViewMapDataSource : RemoteDataSource<Pair<String, String>, SummaryViewMapResponseRemote>

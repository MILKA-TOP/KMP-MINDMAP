package ru.lipt.domain.map

import ru.lipt.core.cache.InMemoryLocalDataSource
import ru.lipt.domain.map.models.abstract.SummaryMapResponseRemote

class MindMapLocalDataSource : InMemoryLocalDataSource<String, SummaryMapResponseRemote>()

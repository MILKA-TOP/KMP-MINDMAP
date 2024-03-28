package ru.lipt.domain.map

import ru.lipt.core.cache.InMemoryLocalDataSource
import ru.lipt.domain.map.models.SummaryViewMapResponseRemote

class MindViewMapLocalDataSource : InMemoryLocalDataSource<Pair<String, String>, SummaryViewMapResponseRemote>()

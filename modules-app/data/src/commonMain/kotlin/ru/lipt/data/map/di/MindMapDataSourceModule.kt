package ru.lipt.data.map.di

import org.koin.dsl.module
import ru.lipt.data.map.MindMapDataSourceImpl
import ru.lipt.domain.map.MindMapDataSource

val mindMapDataSourceModule = module {
    single<MindMapDataSource> { MindMapDataSourceImpl() }
}

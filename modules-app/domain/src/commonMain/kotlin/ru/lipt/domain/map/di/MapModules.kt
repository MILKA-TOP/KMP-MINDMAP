package ru.lipt.domain.map.di

import org.koin.dsl.module
import ru.lipt.domain.map.MindMapInteractor
import ru.lipt.domain.map.MindMapLocalDataSource
import ru.lipt.domain.map.MindMapRepository
import ru.lipt.domain.map.MindMapUpdateLocalDataSource

val mapModules = module {
    single { MindMapLocalDataSource() }
    single { MindMapUpdateLocalDataSource() }
    single {
        MindMapRepository(
            localDataSource = get(),
            remoteDataSource = get(),
            updateDataSource = get()
        )
    }
    single {
        MindMapInteractor(
            mapRepository = get(),
            catalogRepository = get(),
        )
    }
}

package ru.lipt.domain.map.di

import org.koin.dsl.module
import ru.lipt.domain.map.MindMapInteractor
import ru.lipt.domain.map.MindMapLocalDataSource
import ru.lipt.domain.map.MindMapRepository

val mapModules = module {
    single { MindMapLocalDataSource() }
    single {
        MindMapRepository(
            localDataSource = get(),
            remoteDataSource = get()
        )
    }
    single {
        MindMapInteractor(
            mapRepository = get(),
            catalogRepository = get(),
        )
    }
}

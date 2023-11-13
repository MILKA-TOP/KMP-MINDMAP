package ru.lipt.domain.catalog.di

import org.koin.dsl.module
import ru.lipt.domain.catalog.CatalogInteractor
import ru.lipt.domain.catalog.CatalogLocalDataSource
import ru.lipt.domain.catalog.CatalogRepository

val catalogModules = module {
    single { CatalogLocalDataSource() }
    single {
        CatalogRepository(
            localDataSource = get(),
            remoteDataSource = get(),
        )
    }
    single {
        CatalogInteractor(
            catalogRepository = get(),
        )
    }
}

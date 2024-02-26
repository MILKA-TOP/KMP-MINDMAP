package ru.lipt.data.catalog.di

import org.koin.dsl.module
import ru.lipt.core.network.AUTHED_CLIENT_QUALIFIER
import ru.lipt.data.catalog.CatalogDataSourceImpl
import ru.lipt.domain.catalog.CatalogDataSource

val catalogDataSourceModule = module {
    single<CatalogDataSource> {
        CatalogDataSourceImpl(
            client = get(AUTHED_CLIENT_QUALIFIER),
            config = get()
        )
    }
}

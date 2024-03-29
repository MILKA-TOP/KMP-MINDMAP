package ru.lipt.data.catalog.di

import org.koin.dsl.module
import ru.lipt.core.di.USER_SESSION_SCOPE_QUALIFIER
import ru.lipt.core.di.getUserSessionScope
import ru.lipt.core.kover.IgnoreKover
import ru.lipt.core.network.AUTHED_CLIENT_QUALIFIER
import ru.lipt.data.catalog.CatalogDataSourceImpl
import ru.lipt.domain.catalog.CatalogDataSource

@IgnoreKover
val catalogDataSourceModule = module {
    scope(USER_SESSION_SCOPE_QUALIFIER) {
        scoped<CatalogDataSource> {
            CatalogDataSourceImpl(
                client = getUserSessionScope().get(AUTHED_CLIENT_QUALIFIER),
                config = get()
            )
        }
    }
}

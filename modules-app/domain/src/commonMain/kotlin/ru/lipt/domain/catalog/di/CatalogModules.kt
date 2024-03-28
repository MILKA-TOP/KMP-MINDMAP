package ru.lipt.domain.catalog.di

import org.koin.dsl.module
import ru.lipt.core.di.USER_SESSION_SCOPE_QUALIFIER
import ru.lipt.core.di.getUserSessionScope
import ru.lipt.domain.catalog.CatalogInteractor
import ru.lipt.domain.catalog.CatalogLocalDataSource
import ru.lipt.domain.catalog.CatalogRepository

val catalogModules = module {
    scope(USER_SESSION_SCOPE_QUALIFIER) {
        scoped { CatalogLocalDataSource() }
        factory {
            CatalogRepository(
                localDataSource = getUserSessionScope().get(),
                remoteDataSource = getUserSessionScope().get(),
            )
        }
        factory {
            CatalogInteractor(
                catalogRepository = getUserSessionScope().get(),
            )
        }
    }
}

package ru.lipt.catalog.search

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.catalog.navigation.PrivateCatalogDestinations
import ru.lipt.core.di.getUserSessionScope
import ru.lipt.core.kover.IgnoreKover

@IgnoreKover
val searchNavigationModule = module {
    ScreenRegistry.register<PrivateCatalogDestinations.SearchDestination> {
        SearchScreen
    }
    factory {
        SearchScreenModel(
            catalogInteractor = getUserSessionScope().get(),
        )
    }
}

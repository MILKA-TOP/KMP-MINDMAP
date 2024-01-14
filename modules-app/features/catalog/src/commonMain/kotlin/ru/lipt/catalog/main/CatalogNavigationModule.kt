package ru.lipt.catalog.main

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.catalog.common.navigation.CatalogNavigationDestinations

val catalogNavigationModule = module {
    ScreenRegistry.register<CatalogNavigationDestinations.CatalogScreenDestination> {
        CatalogScreen
    }
    factory {
        CatalogScreenModel(
            catalogInteractor = get(),
            loginInteractor = get(),
        )
    }
}

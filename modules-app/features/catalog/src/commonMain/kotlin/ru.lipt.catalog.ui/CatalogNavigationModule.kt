package ru.lipt.catalog.ui

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.catalog.common.navigation.CatalogNavigationDestinations

val catalogNavigationModule = module {
    ScreenRegistry.register<CatalogNavigationDestinations.CatalogScreenDestination> {
        CatalogScreen
    }
    factory {
        CatalogScreenModel(
            catalogInteractor = get()
        )
    }
}

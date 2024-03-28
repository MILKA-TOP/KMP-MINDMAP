package ru.lipt.catalog.main

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.catalog.common.navigation.CatalogNavigationDestinations
import ru.lipt.core.di.getUserSessionScope

val catalogNavigationModule = module {
    ScreenRegistry.register<CatalogNavigationDestinations.CatalogScreenDestination> {
        CatalogScreen
    }
    factory {
        CatalogScreenModel(
            catalogInteractor = getUserSessionScope().get(),
        )
    }
}

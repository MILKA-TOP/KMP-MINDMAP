package ru.lipt.catalog.migrate

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.catalog.common.navigation.CatalogNavigationDestinations
import ru.lipt.core.di.getUserSessionScope
import ru.lipt.core.kover.IgnoreKover

@IgnoreKover
val migrateMindMapNavigationModule = module {
    ScreenRegistry.register<CatalogNavigationDestinations.MigrateScreenDestination> { provider ->
        MigrateMindMapScreen()
    }
    factory { params ->
        MigrateMindMapScreenModel(
            catalogInteractor = getUserSessionScope().get(),
        )
    }
}

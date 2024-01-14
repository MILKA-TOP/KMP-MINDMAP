package ru.lipt.catalog.create

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.catalog.navigation.PrivateCatalogDestinations

val createMindMapNavigationModule = module {
    ScreenRegistry.register<PrivateCatalogDestinations.CreateMapDestination> {
        CreateMindMapScreen
    }
    factory {
        CreateMindMapScreenModel(
            catalogInteractor = get(),
        )
    }
}

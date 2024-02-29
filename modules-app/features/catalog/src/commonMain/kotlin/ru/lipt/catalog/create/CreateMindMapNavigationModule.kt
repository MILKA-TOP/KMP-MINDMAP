package ru.lipt.catalog.create

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.catalog.common.navigation.CatalogNavigationDestinations

val createMindMapNavigationModule = module {
    ScreenRegistry.register<CatalogNavigationDestinations.CreateMapDestination> { provider ->
        CreateMindMapScreen(
            params = provider.params
        )
    }
    factory { params ->
        CreateMindMapScreenModel(
            params = params.get(),
            catalogInteractor = get(),
        )
    }
}

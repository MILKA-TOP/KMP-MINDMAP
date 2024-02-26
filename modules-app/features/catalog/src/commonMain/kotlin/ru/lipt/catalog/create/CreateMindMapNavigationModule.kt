package ru.lipt.catalog.create

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.catalog.navigation.PrivateCatalogDestinations

val createMindMapNavigationModule = module {
    ScreenRegistry.register<PrivateCatalogDestinations.CreateMapDestination> { provider ->
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

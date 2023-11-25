package ru.lipt.map.ui

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.map.common.navigation.MapNavigationDestinations

val mapNavigationModule = module {
    ScreenRegistry.register<MapNavigationDestinations.MapScreenDestination> { provider ->
        MapScreen(provider.params)
    }
    factory { params ->
        MapScreenModel(
            params = params.get(),
            mapInteractor = get(),
        )
    }
}

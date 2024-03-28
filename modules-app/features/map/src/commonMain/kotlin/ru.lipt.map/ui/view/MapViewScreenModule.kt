package ru.lipt.map.ui.view

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.core.di.getUserSessionScope
import ru.lipt.map.common.navigation.MapNavigationDestinations

val mapViewScreenModule = module {
    ScreenRegistry.register<MapNavigationDestinations.MapViewScreenDestination> { provider ->
        MapViewScreen(provider.params)
    }
    factory { params ->
        MapViewScreenModel(
            params = params.get(),
            mapInteractor = getUserSessionScope().get(),
        )
    }
}

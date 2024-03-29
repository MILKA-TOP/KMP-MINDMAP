package ru.lipt.map.ui

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.core.di.getUserSessionScope
import ru.lipt.core.kover.IgnoreKover
import ru.lipt.map.common.navigation.MapNavigationDestinations

@IgnoreKover
val mapScreenModule = module {
    ScreenRegistry.register<MapNavigationDestinations.MapScreenDestination> { provider ->
        MapScreen(provider.params)
    }
    factory { params ->
        MapScreenModel(
            params = params.get(),
            mapInteractor = getUserSessionScope().get(),
        )
    }
}

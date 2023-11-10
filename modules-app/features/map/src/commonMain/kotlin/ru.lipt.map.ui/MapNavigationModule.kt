package ru.lipt.map.ui

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.navigation.MainNavigator

val mapNavigationModule = module {
    ScreenRegistry.register<MainNavigator.MapScreenDestination> { provider ->
        MapScreen(provider.id)
    }
    factory { MapScreenModel() }
}

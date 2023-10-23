package ui.lipt.map.ui

import cafe.adriel.voyager.core.registry.screenModule
import ru.lipt.navigation.MainNavigator

val mapNavigationModule = screenModule {
    register<MainNavigator.MapScreenDestination> {
        MapScreen()
    }
}
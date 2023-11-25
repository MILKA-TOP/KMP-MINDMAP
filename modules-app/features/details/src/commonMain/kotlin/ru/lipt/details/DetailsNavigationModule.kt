package ru.lipt.details

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.navigation.MainNavigator

val detailsNavigationModule = module {
    ScreenRegistry.register<MainNavigator.DetailsScreenDestination> { provider ->
        DetailsScreen()
    }

    factory {
        DetailsScreenModel()
    }
}

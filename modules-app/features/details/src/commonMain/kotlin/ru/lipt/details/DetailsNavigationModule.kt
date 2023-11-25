package ru.lipt.details

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.details.common.navigation.NodeDetailsNavigationDestinations

val detailsNavigationModule = module {
    ScreenRegistry.register<NodeDetailsNavigationDestinations.NodeDetailsScreenDestination> { provider ->
        DetailsScreen(params = provider.params)
    }

    factory {
        DetailsScreenModel()
    }
}

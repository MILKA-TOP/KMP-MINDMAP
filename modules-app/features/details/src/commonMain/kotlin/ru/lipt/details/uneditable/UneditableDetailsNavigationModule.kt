package ru.lipt.details.uneditable

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.core.di.getUserSessionScope
import ru.lipt.details.common.navigation.NodeDetailsNavigationDestinations

val uneditableDetailsNavigationModule = module {
    ScreenRegistry.register<NodeDetailsNavigationDestinations.UneditableNodeDetailsScreenDestination> { provider ->
        UneditableDetailsScreen(params = provider.params)
    }

    factory { params ->
        UneditableDetailsScreenModel(
            params = params.get(),
            mapInteractor = getUserSessionScope().get(),
        )
    }
}

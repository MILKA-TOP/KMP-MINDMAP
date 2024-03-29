package ru.lipt.details.editable

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.core.di.getUserSessionScope
import ru.lipt.core.kover.IgnoreKover
import ru.lipt.details.common.navigation.NodeDetailsNavigationDestinations

@IgnoreKover
val editableDetailsNavigationModule = module {
    ScreenRegistry.register<NodeDetailsNavigationDestinations.EditableNodeDetailsScreenDestination> { provider ->
        EditableDetailsScreen(params = provider.params)
    }

    factory { params ->
        EditableDetailsScreenModel(
            params = params.get(),
            mapInteractor = getUserSessionScope().get()
        )
    }
}

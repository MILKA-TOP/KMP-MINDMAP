package ru.lipt.details.editable

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.details.common.navigation.NodeDetailsNavigationDestinations

val editableDetailsNavigationModule = module {
    ScreenRegistry.register<NodeDetailsNavigationDestinations.EditableNodeDetailsScreenDestination> { provider ->
        EditableDetailsScreen(params = provider.params)
    }

    factory { params ->
        EditableDetailsScreenModel(
            params = params.get(),
            mapInteractor = get()
        )
    }
}

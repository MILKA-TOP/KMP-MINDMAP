package ru.lipt.details

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.details.common.navigation.NodeDetailsNavigationDestinations
import ru.lipt.details.editable.EditableDetailsScreen
import ru.lipt.details.editable.EditableDetailsScreenModel

val detailsNavigationModule = module {
    ScreenRegistry.register<NodeDetailsNavigationDestinations.NodeDetailsScreenDestination> { provider ->
        EditableDetailsScreen(params = provider.params)
    }

    factory { params ->
        EditableDetailsScreenModel(
            params = params.get()
        )
    }
}

package ru.lipt.map.details

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.map.PrivateMapNavigationDestinations

val mapDetailsModule = module {
    ScreenRegistry.register<PrivateMapNavigationDestinations.MapDetails> { provider ->
        MapDetailsScreen(provider.params)
    }
    factory { params ->
        MapDetailsScreenModel(
            params = params.get(),
            mapInteractor = get(),
        )
    }
}

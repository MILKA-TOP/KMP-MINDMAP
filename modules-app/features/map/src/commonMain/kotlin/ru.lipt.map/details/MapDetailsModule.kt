package ru.lipt.map.details

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.map.PrivateMapNavigationDestinations
import ru.lipt.map.details.edit.MapDetailsEditScreen
import ru.lipt.map.details.edit.MapDetailsEditScreenModel

val mapDetailsModule = module {
    ScreenRegistry.register<PrivateMapNavigationDestinations.MapEditDetails> { provider ->
        MapDetailsEditScreen(provider.params)
    }
    factory { params ->
        MapDetailsEditScreenModel(
            params = params.get(),
            mapInteractor = get(),
        )
    }
}

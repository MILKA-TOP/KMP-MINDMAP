package ru.lipt.map.details

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.core.di.getUserSessionScope
import ru.lipt.map.PrivateMapNavigationDestinations
import ru.lipt.map.details.edit.MapDetailsEditScreen
import ru.lipt.map.details.edit.MapDetailsEditScreenModel
import ru.lipt.map.details.view.MapDetailsViewScreen
import ru.lipt.map.details.view.MapDetailsViewScreenModel

val mapDetailsModule = module {
    ScreenRegistry.register<PrivateMapNavigationDestinations.MapEditDetails> { provider ->
        MapDetailsEditScreen(provider.params)
    }
    ScreenRegistry.register<PrivateMapNavigationDestinations.MapViewDetails> { provider ->
        MapDetailsViewScreen(provider.params)
    }
    factory { params ->
        MapDetailsEditScreenModel(
            params = params.get(),
            mapInteractor = getUserSessionScope().get(),
        )
    }
    factory { params ->
        MapDetailsViewScreenModel(
            params = params.get(),
            mapInteractor = getUserSessionScope().get(),
        )
    }
}

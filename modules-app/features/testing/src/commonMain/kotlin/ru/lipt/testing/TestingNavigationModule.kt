package ru.lipt.testing

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.core.di.getUserSessionScope
import ru.lipt.core.kover.IgnoreKover
import ru.lipt.testing.common.navigation.TestingNavigationDestinations
import ru.lipt.testing.complete.TestingCompleteScreen
import ru.lipt.testing.complete.TestingCompleteScreenModel
import ru.lipt.testing.edit.TestingEditScreen
import ru.lipt.testing.edit.TestingEditScreenModel
import ru.lipt.testing.result.TestingResultScreen
import ru.lipt.testing.result.TestingResultScreenModel

@IgnoreKover
val testingNavigationModule = module {
    ScreenRegistry.register<TestingNavigationDestinations.TestEditScreenDestination> { provider ->
        TestingEditScreen(params = provider.params)
    }
    ScreenRegistry.register<TestingNavigationDestinations.TestCompleteScreenDestination> { provider ->
        TestingCompleteScreen(params = provider.params)
    }
    ScreenRegistry.register<TestingNavigationDestinations.TestResultScreenDestination> { provider ->
        TestingResultScreen(params = provider.params)
    }

    factory { params ->
        TestingEditScreenModel(
            params = params.get(),
            mapInteractor = getUserSessionScope().get(),
        )
    }
    factory { params ->
        TestingCompleteScreenModel(
            params = params.get(),
            mapInteractor = getUserSessionScope().get(),
        )
    }
    factory { params ->
        TestingResultScreenModel(
            params = params.get(),
        )
    }
}

package ru.lipt.testing

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.testing.common.navigation.TestingNavigationDestinations
import ru.lipt.testing.edit.TestingEditScreen
import ru.lipt.testing.edit.TestingScreenModel

val testingNavigationModule = module {
    ScreenRegistry.register<TestingNavigationDestinations.TestEditScreenDestination> { provider ->
        TestingEditScreen(params = provider.params)
    }

    factory { params ->
        TestingScreenModel(
            params = params.get()
        )
    }
}

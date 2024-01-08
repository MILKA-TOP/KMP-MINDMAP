package ru.lipt.login

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.login.common.navigation.LoginNavigationDestinations
import ru.lipt.login.hello.HelloScreen
import ru.lipt.login.hello.HelloScreenModel

val loginNavigationModule = module {
    ScreenRegistry.register<LoginNavigationDestinations.HelloScreenDestination> {
        HelloScreen()
    }

    factory { params ->
        HelloScreenModel()
    }
}

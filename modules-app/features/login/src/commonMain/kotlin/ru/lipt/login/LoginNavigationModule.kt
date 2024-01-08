package ru.lipt.login

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.login.common.navigation.LoginNavigationDestinations
import ru.lipt.login.hello.HelloScreen
import ru.lipt.login.hello.HelloScreenModel
import ru.lipt.login.registry.registryDiModule

val loginNavigationModule = module {
    ScreenRegistry.register<LoginNavigationDestinations.HelloScreenDestination> {
        HelloScreen()
    }

    factory { params ->
        HelloScreenModel()
    }
    includes(registryDiModule)
}

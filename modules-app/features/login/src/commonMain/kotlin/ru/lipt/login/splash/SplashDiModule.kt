package ru.lipt.login.splash

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.login.common.navigation.LoginNavigationDestinations

val splashDiModule = module {
    ScreenRegistry.register<LoginNavigationDestinations.SplashScreenDestination> {
        SplashScreen
    }

    factory { params ->
        SplashScreenModel(
            loginInteractor = get()
        )
    }
}

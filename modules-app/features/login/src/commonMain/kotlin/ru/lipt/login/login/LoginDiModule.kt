package ru.lipt.login.login

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.core.kover.IgnoreKover
import ru.lipt.login.navigation.PrivateLoginDestinations

@IgnoreKover
val loginDiModule = module {
    ScreenRegistry.register<PrivateLoginDestinations.LoginDestination> {
        LoginScreen
    }

    factory { params ->
        LoginScreenModel(
            loginInteractor = get()
        )
    }
}

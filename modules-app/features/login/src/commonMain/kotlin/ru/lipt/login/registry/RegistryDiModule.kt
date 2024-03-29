package ru.lipt.login.registry

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.core.kover.IgnoreKover
import ru.lipt.login.navigation.PrivateLoginDestinations
import ru.lipt.login.registry.input.RegistryInputScreen
import ru.lipt.login.registry.input.RegistryInputScreenModel

@IgnoreKover
val registryDiModule = module {
    ScreenRegistry.register<PrivateLoginDestinations.RegistryInputDestination> {
        RegistryInputScreen()
    }

    factory { params ->
        RegistryInputScreenModel(
            loginInteractor = get()
        )
    }
}

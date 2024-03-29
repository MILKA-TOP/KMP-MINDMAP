package ru.lipt.login.pin

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.core.di.getUserSessionScope
import ru.lipt.core.kover.IgnoreKover
import ru.lipt.login.common.navigation.LoginNavigationDestinations
import ru.lipt.login.pin.create.PinPadCreateScreen
import ru.lipt.login.pin.create.PinPadCreateScreenModel
import ru.lipt.login.pin.input.PinPadInputScreen
import ru.lipt.login.pin.input.PinPadInputScreenModel

@IgnoreKover
val pinDiModule = module {
    ScreenRegistry.register<PrivatePinPadDestinations.CreatePin> {
        PinPadCreateScreen()
    }
    ScreenRegistry.register<LoginNavigationDestinations.PinInputScreenDestination> {
        PinPadInputScreen()
    }

    factory { params ->
        PinPadCreateScreenModel(
            loginInteractor = getUserSessionScope().get()
        )
    }
    factory { params ->
        PinPadInputScreenModel(
            loginInteractor = get()
        )
    }
}

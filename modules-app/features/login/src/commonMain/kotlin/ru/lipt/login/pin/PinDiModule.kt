package ru.lipt.login.pin

import cafe.adriel.voyager.core.registry.ScreenRegistry
import org.koin.dsl.module
import ru.lipt.login.pin.create.PinPadCreateScreen
import ru.lipt.login.pin.create.PinPadCreateScreenModel

val pinDiModule = module {
    ScreenRegistry.register<PrivatePinPadDestinations.CreatePin> {
        PinPadCreateScreen()
    }

    factory { params ->
        PinPadCreateScreenModel()
    }
}

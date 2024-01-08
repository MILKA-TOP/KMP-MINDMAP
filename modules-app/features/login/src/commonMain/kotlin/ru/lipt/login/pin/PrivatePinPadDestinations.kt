package ru.lipt.login.pin

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class PrivatePinPadDestinations : ScreenProvider {

    data object CreatePin : PrivatePinPadDestinations()
}

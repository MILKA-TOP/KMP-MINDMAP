package ru.lipt.login.registry

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class PrivateLoginDestinations : ScreenProvider {

    data object RegistryInputDestination : PrivateLoginDestinations()
}

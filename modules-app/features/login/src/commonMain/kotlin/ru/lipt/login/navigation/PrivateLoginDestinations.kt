package ru.lipt.login.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class PrivateLoginDestinations : ScreenProvider {

    data object RegistryInputDestination : PrivateLoginDestinations()
}

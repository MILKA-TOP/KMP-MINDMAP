package ru.lipt.testing.common.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider
import ru.lipt.testing.common.params.TestEditScreenParams

sealed class TestingNavigationDestinations : ScreenProvider {

    data class TestEditScreenDestination(val params: TestEditScreenParams) :
        TestingNavigationDestinations()
}

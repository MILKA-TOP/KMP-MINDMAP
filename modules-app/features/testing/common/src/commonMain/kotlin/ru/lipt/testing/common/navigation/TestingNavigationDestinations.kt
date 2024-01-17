package ru.lipt.testing.common.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider
import ru.lipt.testing.common.params.TestCompleteScreenParams
import ru.lipt.testing.common.params.TestEditScreenParams
import ru.lipt.testing.common.params.TestingResultParams

sealed class TestingNavigationDestinations : ScreenProvider {

    data class TestEditScreenDestination(val params: TestEditScreenParams) :
        TestingNavigationDestinations()
    data class TestCompleteScreenDestination(val params: TestCompleteScreenParams) :
        TestingNavigationDestinations()
    data class TestResultScreenDestination(val params: TestingResultParams) :
        TestingNavigationDestinations()
}

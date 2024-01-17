package ru.lipt.testing.complete

import ru.lipt.testing.common.params.TestingResultParams

sealed class NavigationTarget {
    data class Result(val params: TestingResultParams) : NavigationTarget()
}

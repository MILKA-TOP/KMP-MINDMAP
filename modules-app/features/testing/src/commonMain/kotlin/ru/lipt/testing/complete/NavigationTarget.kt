package ru.lipt.testing.complete

sealed class NavigationTarget {
    data object Result : NavigationTarget()
}

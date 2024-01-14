package ru.lipt.login.hello

sealed class NavigationTarget {
    data object RegistryNavigate : NavigationTarget()
    data object LoginNavigate : NavigationTarget()
}

package ru.lipt.login.splash

sealed class NavigationTarget {
    object HelloScreenNavigate : NavigationTarget()
    object PinInputScreenNavigate : NavigationTarget()
}

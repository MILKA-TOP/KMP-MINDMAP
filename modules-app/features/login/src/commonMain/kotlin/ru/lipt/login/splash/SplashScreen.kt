package ru.lipt.login.splash

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

object SplashScreen : Screen {
    @Composable
    override fun Content() {
        SplashContent(screen = this)
    }
}

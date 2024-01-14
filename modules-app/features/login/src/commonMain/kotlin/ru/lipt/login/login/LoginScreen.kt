package ru.lipt.login.login

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen

object LoginScreen : Screen {

    @Composable
    override fun Content() {
        LoginContent(screen = this)
    }
}

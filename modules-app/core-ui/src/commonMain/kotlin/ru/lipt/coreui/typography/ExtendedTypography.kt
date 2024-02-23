package ru.lipt.coreui.typography

import androidx.compose.material.Typography
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.text.TextStyle

@Immutable
data class ExtendedTypography(
    val material: Typography = Typography(),
    val badge: TextStyle = TextStyle.Default,
    val link1: TextStyle = TextStyle.Default,
    val link2: TextStyle = TextStyle.Default,
    val link3: TextStyle = TextStyle.Default,
) {

    internal companion object {
        val LocalExtendedTypography = staticCompositionLocalOf { ExtendedTypography() }
    }
}

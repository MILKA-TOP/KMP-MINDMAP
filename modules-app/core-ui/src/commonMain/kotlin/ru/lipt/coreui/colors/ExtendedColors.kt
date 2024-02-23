package ru.lipt.coreui.colors

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val onBackgroundSecondary: Color = Color.Unspecified,
    val surface: Surface = Surface(),
    val success: Color = Color.Unspecified,
    val warning: Color = Color.Unspecified,
    val onWarning: Color = Color.Unspecified,
) {

    val material: Colors
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colors

    @Immutable
    data class Surface(
        val secondary: Color = Color.Unspecified,
        val onSurfaceSecondary: Color = Color.Unspecified,
    )
}

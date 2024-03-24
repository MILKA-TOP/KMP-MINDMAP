package ru.lipt.coreui.colors

import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import ru.lipt.coreui.colors.Palette.Green50
import ru.lipt.coreui.colors.Palette.Green500
import ru.lipt.coreui.colors.Palette.Red50
import ru.lipt.coreui.colors.Palette.Red600

@Immutable
data class ExtendedColors(
    val onBackgroundSecondary: Color = Color.Unspecified,
    val surface: Surface = Surface(),
    val unmarkedNode: Color = Color.Unspecified,
    val successLight: Color = Green50,
    val success: Color = Green500,
    val warning: Color = Color.Unspecified,
    val errorLight: Color = Red50,
    val error: Color = Red600,
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

    internal companion object {
        val LocalExtendedColors = staticCompositionLocalOf {
            ExtendedColors(
                successLight = Green500,
                errorLight = Red600,
            )
        }
    }
}

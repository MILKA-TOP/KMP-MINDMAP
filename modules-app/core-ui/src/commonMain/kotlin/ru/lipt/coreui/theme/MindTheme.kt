package ru.lipt.coreui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import ru.lipt.coreui.colors.MindColors
import ru.lipt.coreui.shapes.MindShapes
import ru.lipt.coreui.typography.ExtendedTypography
import ru.lipt.coreui.typography.ExtendedTypography.Companion.LocalExtendedTypography
import ru.lipt.coreui.typography.MindTypography

@Composable
fun MindTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colors = MindColors.provideMaterial(),
        typography = MindTypography.Material,
        shapes = MindShapes
    ) {
        CompositionLocalProvider(
            // app typography
            LocalExtendedTypography provides MindTypography.Extended,
            content = content
        )
    }
}

object MindTheme {

    val typography: ExtendedTypography
        @Composable
        @ReadOnlyComposable
        get() = LocalExtendedTypography.current
}

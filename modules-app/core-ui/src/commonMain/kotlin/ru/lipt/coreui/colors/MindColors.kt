package ru.lipt.coreui.colors

import androidx.compose.material.Colors
import androidx.compose.material.lightColors
import ru.lipt.coreui.colors.Palette.Black
import ru.lipt.coreui.colors.Palette.Green50
import ru.lipt.coreui.colors.Palette.Green500
import ru.lipt.coreui.colors.Palette.Green700
import ru.lipt.coreui.colors.Palette.Orange600
import ru.lipt.coreui.colors.Palette.Orange800
import ru.lipt.coreui.colors.Palette.Red600
import ru.lipt.coreui.colors.Palette.White

object MindColors {

    private val Light = lightColors(
        primary = Green500,
        primaryVariant = Green700,
        secondary = Orange600,
        secondaryVariant = Orange800,
        background = White,
        surface = Green50,
        error = Red600,
        onPrimary = Black,
        onSecondary = Black,
        onBackground = Black,
        onSurface = Black,
        onError = White
    )

    internal fun provideMaterial(): Colors = Light

    val Extended = ExtendedColors(
        unmarkedNode = Palette.GrayLight
    )
}

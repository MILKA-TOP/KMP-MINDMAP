package ru.lipt.coreui.icons

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

@Composable
fun FingerTouchIcon(): ImageVector {
    return remember {
        ImageVector.Builder(
            name = "touch_app",
            defaultWidth = 40.0.dp,
            defaultHeight = 40.0.dp,
            viewportWidth = 40.0f,
            viewportHeight = 40.0f
        ).apply {
            path(
                fill = SolidColor(Color.Black),
                fillAlpha = 1f,
                stroke = null,
                strokeAlpha = 1f,
                strokeLineWidth = 1.0f,
                strokeLineCap = StrokeCap.Butt,
                strokeLineJoin = StrokeJoin.Miter,
                strokeLineMiter = 1f,
                pathFillType = PathFillType.NonZero
            ) {
                moveTo(19.333f, 1.958f)
                quadToRelative(3.709f, 0f, 6.334f, 2.584f)
                quadToRelative(2.625f, 2.583f, 2.625f, 6.291f)
                quadToRelative(0f, 2.209f, -1f, 4.105f)
                quadToRelative(-1f, 1.895f, -2.709f, 3.187f)
                horizontalLineToRelative(-1.25f)
                verticalLineToRelative(-2.333f)
                quadToRelative(1.125f, -0.917f, 1.729f, -2.209f)
                quadToRelative(0.605f, -1.291f, 0.605f, -2.75f)
                quadToRelative(0f, -2.625f, -1.855f, -4.437f)
                quadToRelative(-1.854f, -1.813f, -4.479f, -1.813f)
                quadToRelative(-2.666f, 0f, -4.521f, 1.813f)
                quadToRelative(-1.854f, 1.812f, -1.854f, 4.437f)
                quadToRelative(0f, 1.459f, 0.604f, 2.771f)
                quadToRelative(0.605f, 1.313f, 1.73f, 2.229f)
                verticalLineToRelative(3.084f)
                quadToRelative(-2.334f, -1.167f, -3.646f, -3.334f)
                quadToRelative(-1.313f, -2.166f, -1.313f, -4.75f)
                quadToRelative(0f, -3.708f, 2.625f, -6.291f)
                quadToRelative(2.625f, -2.584f, 6.375f, -2.584f)
                close()
                moveTo(17.75f, 36.375f)
                quadToRelative(-0.708f, 0f, -1.292f, -0.25f)
                quadToRelative(-0.583f, -0.25f, -1.041f, -0.708f)
                lineTo(8.75f, 28.75f)
                quadToRelative(-0.625f, -0.625f, -0.604f, -1.729f)
                quadToRelative(0.021f, -1.104f, 0.771f, -1.979f)
                quadToRelative(0.708f, -0.917f, 1.583f, -1.167f)
                reflectiveQuadToRelative(1.917f, -0.042f)
                lineToRelative(2.875f, 0.667f)
                verticalLineTo(10.917f)
                quadToRelative(0f, -1.709f, 1.166f, -2.875f)
                quadToRelative(1.167f, -1.167f, 2.875f, -1.167f)
                quadToRelative(1.667f, 0f, 2.834f, 1.167f)
                quadToRelative(1.166f, 1.166f, 1.166f, 2.875f)
                verticalLineToRelative(7.166f)
                horizontalLineToRelative(1.084f)
                quadToRelative(0.208f, 0f, 0.375f, 0.084f)
                quadToRelative(0.166f, 0.083f, 0.375f, 0.166f)
                lineToRelative(6.083f, 2.959f)
                quadToRelative(0.958f, 0.458f, 1.438f, 1.479f)
                quadToRelative(0.479f, 1.021f, 0.27f, 2.104f)
                lineTo(31.5f, 33.75f)
                quadToRelative(-0.208f, 1.167f, -1.125f, 1.896f)
                quadToRelative(-0.917f, 0.729f, -2.042f, 0.729f)
                close()
                moveToRelative(-0.25f, -2.625f)
                horizontalLineToRelative(11.292f)
                lineToRelative(1.708f, -9.875f)
                lineToRelative(-7.333f, -3.625f)
                horizontalLineToRelative(-2.459f)
                verticalLineToRelative(-9.333f)
                quadToRelative(0f, -0.625f, -0.375f, -1f)
                reflectiveQuadToRelative(-1.041f, -0.375f)
                quadToRelative(-0.625f, 0f, -1f, 0.375f)
                reflectiveQuadToRelative(-0.375f, 1f)
                verticalLineToRelative(16.791f)
                lineToRelative(-6.542f, -1.416f)
                lineToRelative(-0.667f, 0.666f)
                close()
                moveToRelative(11.292f, 0f)
                horizontalLineTo(17.5f)
                horizontalLineToRelative(11.292f)
                close()
            }
        }.build()
    }
}

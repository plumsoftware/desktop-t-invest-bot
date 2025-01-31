package ru.plumsoftware.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

@Immutable
data class ExtendedColors(
    val successContainer: Color,
    val onSuccessContainer: Color,
)

val LocalExtendedColors = staticCompositionLocalOf {
    ExtendedColors(
        successContainer = Color.Unspecified,
        onSuccessContainer = Color.Unspecified,
    )
}
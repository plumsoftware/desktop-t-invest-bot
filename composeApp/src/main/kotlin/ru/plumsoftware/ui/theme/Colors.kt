package ru.plumsoftware.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

val primaryLight = Color(0xFF8D4F00)
val onPrimaryLight = Color(0xFFFFFFFF)
val primaryContainerLight = Color(0xFFFFB46D)
val onPrimaryContainerLight = Color(0xFF512B00)
val secondaryLight = Color(0xFF7B5734)
val onSecondaryLight = Color(0xFFFFFFFF)
val secondaryContainerLight = Color(0xFFFFD1AA)
val onSecondaryContainerLight = Color(0xFF5C3B1C)
val tertiaryLight = Color(0xFF5C6300)
val onTertiaryLight = Color(0xFFFFFFFF)
val tertiaryContainerLight = Color(0xFFC2CC5D)
val onTertiaryContainerLight = Color(0xFF333800)
val errorLight = Color(0xFFBA1A1A)
val onErrorLight = Color(0xFFFFFFFF)
val errorContainerLight = Color(0xFFFFDAD6)
val onErrorContainerLight = Color(0xFF410002)
val backgroundLight = Color(0xFFFFF8F5)
val onBackgroundLight = Color(0xFF211A14)
val surfaceLight = Color(0xFFFFF8F5)
val onSurfaceLight = Color(0xFF211A14)
val surfaceVariantLight = Color(0xFFF5DECD)
val onSurfaceVariantLight = Color(0xFF534437)
val outlineLight = Color(0xFF867465)
val outlineVariantLight = Color(0xFFD8C3B2)
val scrimLight = Color(0xFF000000)
val inverseSurfaceLight = Color(0xFF372F28)
val inverseOnSurfaceLight = Color(0xFFFDEEE3)
val inversePrimaryLight = Color(0xFFFFB875)
val surfaceDimLight = Color(0xFFE6D7CD)
val surfaceBrightLight = Color(0xFFFFF8F5)
val surfaceContainerLowestLight = Color(0xFFFFFFFF)
val surfaceContainerLowLight = Color(0xFFFFF1E8)
val surfaceContainerLight = Color(0xFFFAEBE1)
val surfaceContainerHighLight = Color(0xFFF5E5DB)
val surfaceContainerHighestLight = Color(0xFFEFE0D5)

val primaryDark = Color(0xFFFFD9B9)
val onPrimaryDark = Color(0xFF4B2800)
val primaryContainerDark = Color(0xFFF9A34C)
val onPrimaryContainerDark = Color(0xFF422200)
val secondaryDark = Color(0xFFEDBD93)
val onSecondaryDark = Color(0xFF472A0B)
val secondaryContainerDark = Color(0xFF573718)
val onSecondaryContainerDark = Color(0xFFFAC99E)
val tertiaryDark = Color(0xFFDDE775)
val onTertiaryDark = Color(0xFF2F3300)
val tertiaryContainerDark = Color(0xFFB3BD50)
val onTertiaryContainerDark = Color(0xFF282C00)
val errorDark = Color(0xFFFFB4AB)
val onErrorDark = Color(0xFF690005)
val errorContainerDark = Color(0xFF93000A)
val onErrorContainerDark = Color(0xFFFFDAD6)
val backgroundDark = Color(0xFF19120C)
val onBackgroundDark = Color(0xFFEFE0D5)
val surfaceDark = Color(0xFF19120C)
val onSurfaceDark = Color(0xFFEFE0D5)
val surfaceVariantDark = Color(0xFF534437)
val onSurfaceVariantDark = Color(0xFFD8C3B2)
val outlineDark = Color(0xFFA18D7E)
val outlineVariantDark = Color(0xFF534437)
val scrimDark = Color(0xFF000000)
val inverseSurfaceDark = Color(0xFFEFE0D5)
val inverseOnSurfaceDark = Color(0xFF372F28)
val inversePrimaryDark = Color(0xFF8D4F00)
val surfaceDimDark = Color(0xFF19120C)
val surfaceBrightDark = Color(0xFF403830)
val surfaceContainerLowestDark = Color(0xFF130D08)
val surfaceContainerLowDark = Color(0xFF211A14)
val surfaceContainerDark = Color(0xFF261E18)
val surfaceContainerHighDark = Color(0xFF312822)
val surfaceContainerHighestDark = Color(0xFF3C332C)


val seed = Color(0xFFB3B3E9)
val test = Color(0xFFFBA700)
val light_test = Color(0xFF825500)
val light_ontest = Color(0xFFFFFFFF)
val light_testContainer = Color(0xFFFFDDB4)
val light_ontestContainer = Color(0xFF291800)
val dark_test = Color(0xFFFFB953)
val dark_ontest = Color(0xFF452B00)
val dark_testContainer = Color(0xFF633F00)
val dark_ontestContainer = Color(0xFFFFDDB4)

val md_theme_light_success = Color(0xFFCDEDA3)
val md_theme_light_onSuccess = Color(0xFF5aad07)

val md_theme_dark_success = Color(0xFF354E16)
val md_theme_dark_onSuccess = Color(0xFFCDEDA3)

val md_theme_light_line_chart_color = primaryLight
val md_theme_light_point_chart_color = Color(250, 114, 2)

val md_theme_dark_line_chart_color = primaryDark
val md_theme_dark_point_chart_color = Color(250, 114, 2)

val md_theme_light_axis_line_chart_color = onBackgroundLight.copy(alpha = 0.5f)
val md_theme_dark_axis_line_chart_color = onBackgroundDark.copy(alpha = 0.5f)

//region::Constant colors
val trafficLightColor = Color(0xFF2F4859)
val trafficLightBranchColor = Color(0xFF293B44)

object RoadColor {
    val roadSide = Color(0xFFb0b8c1)
}
//endregion

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


internal val lightScheme = lightColorScheme(
    primary = primaryLight,
    onPrimary = onPrimaryLight,
    primaryContainer = primaryContainerLight,
    onPrimaryContainer = onPrimaryContainerLight,
    secondary = secondaryLight,
    onSecondary = onSecondaryLight,
    secondaryContainer = secondaryContainerLight,
    onSecondaryContainer = onSecondaryContainerLight,
    tertiary = tertiaryLight,
    onTertiary = onTertiaryLight,
    tertiaryContainer = tertiaryContainerLight,
    onTertiaryContainer = onTertiaryContainerLight,
    error = errorLight,
    onError = onErrorLight,
    errorContainer = errorContainerLight,
    onErrorContainer = onErrorContainerLight,
    background = backgroundLight,
    onBackground = onBackgroundLight,
    surface = surfaceLight,
    onSurface = onSurfaceLight,
    surfaceVariant = surfaceVariantLight,
    onSurfaceVariant = onSurfaceVariantLight,
    outline = outlineLight,
    inverseSurface = inverseSurfaceLight,
    inverseOnSurface = inverseOnSurfaceLight,
    inversePrimary = inversePrimaryLight,
)

internal val darkScheme = darkColorScheme(
    primary = primaryDark,
    onPrimary = onPrimaryDark,
    primaryContainer = primaryContainerDark,
    onPrimaryContainer = onPrimaryContainerDark,
    secondary = secondaryDark,
    onSecondary = onSecondaryDark,
    secondaryContainer = secondaryContainerDark,
    onSecondaryContainer = onSecondaryContainerDark,
    tertiary = tertiaryDark,
    onTertiary = onTertiaryDark,
    tertiaryContainer = tertiaryContainerDark,
    onTertiaryContainer = onTertiaryContainerDark,
    error = errorDark,
    onError = onErrorDark,
    errorContainer = errorContainerDark,
    onErrorContainer = onErrorContainerDark,
    background = backgroundDark,
    onBackground = onBackgroundDark,
    surface = surfaceDark,
    onSurface = onSurfaceDark,
    surfaceVariant = surfaceVariantDark,
    onSurfaceVariant = onSurfaceVariantDark,
    outline = outlineDark,
    inverseSurface = inverseSurfaceDark,
    inverseOnSurface = inverseOnSurfaceDark,
    inversePrimary = inversePrimaryDark,
)
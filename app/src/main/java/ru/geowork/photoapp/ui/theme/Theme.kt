package ru.geowork.photoapp.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColors(
    primary = Primary,
    secondary = Primary
)

private val LightColorScheme = lightColors(
    primary = Primary,
    secondary = Primary
)

private val extendedLightColorScheme = AppColors(
    material = LightColorScheme,
    contentConstant = ContentConstant,
    contentPrimary = ContentPrimaryLight,
    contentSubPrimary = ContentSubPrimaryLight,
    contentSecondary = ContentSecondaryLight,
    contentDisabled = ContentDisabledLight,
    contentBorder = ContentBorderLight,
    contentBackground = ContentBackgroundLight,
    accentPrimary = AccentPrimaryLight,
    accentSubPrimary = AccentSubPrimaryLight,
    accentSecondary = AccentSecondaryLight,
    accentDisabled = AccentDisabledLight,
    accentBorder = AccentBorderLight,
    accentBackground = AccentBackgroundLight,
    backgroundPrimary = BackgroundPrimaryLight,
    backgroundSecondary = BackgroundSecondaryLight,
    backgroundModal = BackgroundModalLight,
    overlayDark = OverlayLightLight,
    overlayLight = OverlayLightLight,
    red = Red,
    green = Green,
    purple = Purple,
    orange = Orange,
    blue = Blue
)

private val extendedDarkColorScheme = AppColors(
    material = DarkColorScheme,
    contentConstant = ContentConstant,
    contentPrimary = ContentPrimaryDark,
    contentSubPrimary = ContentSubPrimaryDark,
    contentSecondary = ContentSecondaryDark,
    contentDisabled = ContentDisabledDark,
    contentBorder = ContentBorderDark,
    contentBackground = ContentBackgroundDark,
    accentPrimary = AccentPrimaryDark,
    accentSubPrimary = AccentSubPrimaryDark,
    accentSecondary = AccentSecondaryDark,
    accentDisabled = AccentDisabledDark,
    accentBorder = AccentBorderDark,
    accentBackground = AccentBackgroundDark,
    backgroundPrimary = BackgroundPrimaryDark,
    backgroundSecondary = BackgroundSecondaryDark,
    backgroundModal = BackgroundModalDark,
    overlayDark = OverlayLightDark,
    overlayLight = OverlayLightDark,
    red = Red,
    green = Green,
    purple = Purple,
    orange = Orange,
    blue = Blue
)

private val extendedTypography = AppTypography()

val LocalExtendedColorScheme = staticCompositionLocalOf { extendedLightColorScheme }
val LocalExtendedTypography = staticCompositionLocalOf { extendedTypography }

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val extendedColorScheme = if (darkTheme) extendedDarkColorScheme else extendedLightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = extendedColorScheme.material.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    CompositionLocalProvider(
        LocalExtendedColorScheme provides extendedColorScheme,
        LocalExtendedTypography provides extendedTypography
    ) {
        MaterialTheme(
            colors = extendedColorScheme.material,
            typography = extendedTypography.material,
            content = content
        )
    }
}

object AppTheme {
    val colors: AppColors
        @Composable
        get() = LocalExtendedColorScheme.current

    val typography: AppTypography
        @Composable
        get() = LocalExtendedTypography.current
}

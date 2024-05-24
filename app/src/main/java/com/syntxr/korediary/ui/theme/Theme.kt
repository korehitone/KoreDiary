package com.syntxr.korediary.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import com.syntxr.korediary.data.kotpref.GlobalPreferences.AppTheme

private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey40,
    tertiary = Pink40



    /* Other default colors to override
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    */
)

// mountain
//light
private val MountainLightScheme = lightColorScheme(
    primary = primaryLightMountain,
    onPrimary = onPrimaryLightMountain,
    primaryContainer = primaryContainerLightMountain,
    onPrimaryContainer = onPrimaryContainerLightMountain,
    secondary = secondaryLightMountain,
    onSecondary = onSecondaryLightMountain,
    secondaryContainer = secondaryContainerLightMountain,
    onSecondaryContainer = onSecondaryContainerLightMountain,
    tertiary = tertiaryLightMountain,
    onTertiary = onTertiaryLightMountain,
    tertiaryContainer = tertiaryContainerLightMountain,
    onTertiaryContainer = onTertiaryContainerLightMountain,
    error = errorLightMountain,
    onError = onErrorLightMountain,
    errorContainer = errorContainerLightMountain,
    onErrorContainer = onErrorContainerLightMountain,
    background = backgroundLightMountain,
    onBackground = onBackgroundLightMountain,
    surface = surfaceLightMountain,
    onSurface = onSurfaceLightMountain,
    surfaceVariant = surfaceVariantLightMountain,
    onSurfaceVariant = onSurfaceVariantLightMountain,
    outline = outlineLightMountain,
    outlineVariant = outlineVariantLightMountain,
    scrim = scrimLightMountain,
    inverseSurface = inverseSurfaceLightMountain,
    inverseOnSurface = inverseOnSurfaceLightMountain,
    inversePrimary = inversePrimaryLightMountain,
    surfaceDim = surfaceDimLightMountain,
    surfaceBright = surfaceBrightLightMountain,
    surfaceContainerLowest = surfaceContainerLowestLightMountain,
    surfaceContainerLow = surfaceContainerLowLightMountain,
    surfaceContainer = surfaceContainerLightMountain,
    surfaceContainerHigh = surfaceContainerHighLightMountain,
    surfaceContainerHighest = surfaceContainerHighestLightMountain,
)
// dark
private val MountainDarkScheme = darkColorScheme(
    primary = primaryDarkMountain,
    onPrimary = onPrimaryDarkMountain,
    primaryContainer = primaryContainerDarkMountain,
    onPrimaryContainer = onPrimaryContainerDarkMountain,
    secondary = secondaryDarkMountain,
    onSecondary = onSecondaryDarkMountain,
    secondaryContainer = secondaryContainerDarkMountain,
    onSecondaryContainer = onSecondaryContainerDarkMountain,
    tertiary = tertiaryDarkMountain,
    onTertiary = onTertiaryDarkMountain,
    tertiaryContainer = tertiaryContainerDarkMountain,
    onTertiaryContainer = onTertiaryContainerDarkMountain,
    error = errorDarkMountain,
    onError = onErrorDarkMountain,
    errorContainer = errorContainerDarkMountain,
    onErrorContainer = onErrorContainerDarkMountain,
    background = backgroundDarkMountain,
    onBackground = onBackgroundDarkMountain,
    surface = surfaceDarkMountain,
    onSurface = onSurfaceDarkMountain,
    surfaceVariant = surfaceVariantDarkMountain,
    onSurfaceVariant = onSurfaceVariantDarkMountain,
    outline = outlineDarkMountain,
    outlineVariant = outlineVariantDarkMountain,
    scrim = scrimDarkMountain,
    inverseSurface = inverseSurfaceDarkMountain,
    inverseOnSurface = inverseOnSurfaceDarkMountain,
    inversePrimary = inversePrimaryDarkMountain,
    surfaceDim = surfaceDimDarkMountain,
    surfaceBright = surfaceBrightDarkMountain,
    surfaceContainerLowest = surfaceContainerLowestDarkMountain,
    surfaceContainerLow = surfaceContainerLowDarkMountain,
    surfaceContainer = surfaceContainerDarkMountain,
    surfaceContainerHigh = surfaceContainerHighDarkMountain,
    surfaceContainerHighest = surfaceContainerHighestDarkMountain,
)

// sakura
private val SakuraLightScheme = lightColorScheme(
    primary = primaryLightSakura,
    onPrimary = onPrimaryLightSakura,
    primaryContainer = primaryContainerLightSakura,
    onPrimaryContainer = onPrimaryContainerLightSakura,
    secondary = secondaryLightSakura,
    onSecondary = onSecondaryLightSakura,
    secondaryContainer = secondaryContainerLightSakura,
    onSecondaryContainer = onSecondaryContainerLightSakura,
    tertiary = tertiaryLightSakura,
    onTertiary = onTertiaryLightSakura,
    tertiaryContainer = tertiaryContainerLightSakura,
    onTertiaryContainer = onTertiaryContainerLightSakura,
    error = errorLightSakura,
    onError = onErrorLightSakura,
    errorContainer = errorContainerLightSakura,
    onErrorContainer = onErrorContainerLightSakura,
    background = backgroundLightSakura,
    onBackground = onBackgroundLightSakura,
    surface = surfaceLightSakura,
    onSurface = onSurfaceLightSakura,
    surfaceVariant = surfaceVariantLightSakura,
    onSurfaceVariant = onSurfaceVariantLightSakura,
    outline = outlineLightSakura,
    outlineVariant = outlineVariantLightSakura,
    scrim = scrimLightSakura,
    inverseSurface = inverseSurfaceLightSakura,
    inverseOnSurface = inverseOnSurfaceLightSakura,
    inversePrimary = inversePrimaryLightSakura,
    surfaceDim = surfaceDimLightSakura,
    surfaceBright = surfaceBrightLightSakura,
    surfaceContainerLowest = surfaceContainerLowestLightSakura,
    surfaceContainerLow = surfaceContainerLowLightSakura,
    surfaceContainer = surfaceContainerLightSakura,
    surfaceContainerHigh = surfaceContainerHighLightSakura,
    surfaceContainerHighest = surfaceContainerHighestLightSakura,
)

private val SakuraDarkScheme = darkColorScheme(
    primary = primaryDarkSakura,
    onPrimary = onPrimaryDarkSakura,
    primaryContainer = primaryContainerDarkSakura,
    onPrimaryContainer = onPrimaryContainerDarkSakura,
    secondary = secondaryDarkSakura,
    onSecondary = onSecondaryDarkSakura,
    secondaryContainer = secondaryContainerDarkSakura,
    onSecondaryContainer = onSecondaryContainerDarkSakura,
    tertiary = tertiaryDarkSakura,
    onTertiary = onTertiaryDarkSakura,
    tertiaryContainer = tertiaryContainerDarkSakura,
    onTertiaryContainer = onTertiaryContainerDarkSakura,
    error = errorDarkSakura,
    onError = onErrorDarkSakura,
    errorContainer = errorContainerDarkSakura,
    onErrorContainer = onErrorContainerDarkSakura,
    background = backgroundDarkSakura,
    onBackground = onBackgroundDarkSakura,
    surface = surfaceDarkSakura,
    onSurface = onSurfaceDarkSakura,
    surfaceVariant = surfaceVariantDarkSakura,
    onSurfaceVariant = onSurfaceVariantDarkSakura,
    outline = outlineDarkSakura,
    outlineVariant = outlineVariantDarkSakura,
    scrim = scrimDarkSakura,
    inverseSurface = inverseSurfaceDarkSakura,
    inverseOnSurface = inverseOnSurfaceDarkSakura,
    inversePrimary = inversePrimaryDarkSakura,
    surfaceDim = surfaceDimDarkSakura,
    surfaceBright = surfaceBrightDarkSakura,
    surfaceContainerLowest = surfaceContainerLowestDarkSakura,
    surfaceContainerLow = surfaceContainerLowDarkSakura,
    surfaceContainer = surfaceContainerDarkSakura,
    surfaceContainerHigh = surfaceContainerHighDarkSakura,
    surfaceContainerHighest = surfaceContainerHighestDarkSakura,
)


@Composable
fun KoreDiaryTheme(
    appTheme: AppTheme,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    val colorScheme = when(appTheme){
        AppTheme.DEFAULT_LIGHT -> LightColorScheme
        AppTheme.DEFAULT_DARK -> DarkColorScheme
        AppTheme.MOUNTAIN_LIGHT -> MountainLightScheme
        AppTheme.MOUNTAIN_DARK -> MountainDarkScheme
        AppTheme.SAKURA_LIGHT -> SakuraLightScheme
        AppTheme.SAKURA_DARK -> SakuraDarkScheme
        AppTheme.DYNAMIC -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            } else {
                if (darkTheme) DarkColorScheme else LightColorScheme
            }
        }
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
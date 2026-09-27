package ltd.kyss.petme.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val SanrioLightColorScheme = lightColorScheme(
    primary = SanrioSkyBlueDark,
    onPrimary = Color.White,
    primaryContainer = SanrioSkyBlueLight,
    onPrimaryContainer = SanrioSkyBlueDark,
    secondary = SanrioAccentGreen,
    onSecondary = Color.White,
    secondaryContainer = SanrioGreenBg,
    onSecondaryContainer = Color(0xFF004D40),
    tertiary = SanrioAccentOrange,
    onTertiary = Color.White,
    tertiaryContainer = SanrioOrangeBg,
    onTertiaryContainer = SanrioGoldText,
    background = SanrioSkyBlueLight,
    surface = SanrioSoftWhite,
    onBackground = SanrioTextDark,
    onSurface = SanrioTextDark
)

private val SanrioDarkColorScheme = darkColorScheme(
    primary = SanrioSkyBlue,
    onPrimary = Color.White,
    secondary = SanrioAccentGreen,
    tertiary = SanrioAccentOrange,
    background = Color(0xFF1A2634),
    surface = Color(0xFF243342),
    onBackground = Color.White,
    onSurface = Color.White
)

@Composable
fun PetMeTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Always use our custom vibrant game palette, ignore OS wallpaper tints
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        darkTheme -> SanrioDarkColorScheme
        else -> SanrioLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
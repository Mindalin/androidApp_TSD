package com.example.tsdmanager.ui.theme

import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val DarkColorScheme = darkColorScheme(
    primary = ButtonBackground, // #3a3248 для кнопок
    onPrimary = WhiteText, // #f9f9f9
    secondary = DarkSecondary, // #5b5b85
    onSecondary = WhiteText,
    tertiary = DarkTertiary, // #ffa500
    onTertiary = WhiteText,
    background = DarkBackground, // #2a2238
    onBackground = WhiteText,
    surface = DarkSurface, // #231e2c
    onSurface = WhiteText,
    surfaceVariant = DarkSurface,
    onSurfaceVariant = WhiteText,
    outline = GreyOutline, // #ccc
    error = ErrorColor,
    onError = WhiteText
)

private val LightColorScheme = lightColorScheme(
    primary = ButtonBackground, // #3a3248 для кнопок
    secondary = LightSecondary, // #625b71
    tertiary = LightTertiary, // #7D5260
    background = LightBackground, // #FFFFFBFE
    surface = LightBackground,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = Color.White,
    onBackground = DarkText, // #1C1B1F
    onSurface = DarkText,
    surfaceVariant = LightBackground,
    onSurfaceVariant = DarkText,
    outline = GreyOutline, // #ccc
    error = ErrorColor,
    onError = Color.White
)


@Composable
fun TSDManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    Log.d("TSDManagerTheme", "Applying theme, darkTheme=$darkTheme, background=${colorScheme.background}")

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
package com.andreich.news.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val Primary = Color.White
val Secondary = Color.LightGray
val Error = Color.Red

internal val LightColors = lightColorScheme(
    primary = Primary,
    secondary = Secondary,
    error = Error,
    background = Color(0xFFFFFBFE),
    surface = Color(0xFFFFFBFE),
    onPrimary = Color.Black,
    onSecondary = Color.Black,
    onTertiary = Color.Black,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    onSecondaryContainer = Color.LightGray
)

internal val DarkColors = darkColorScheme(
    primary = Color.Black,
    secondary = Color.DarkGray,
    error = Color.Yellow,
    background = Color(0x00000BFE),
    surface = Color(0x00000BFE),
    onPrimary = Color.White,
    onSecondary = Color.Gray,
    onTertiary = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    onSecondaryContainer = Color.LightGray
)
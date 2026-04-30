package com.emobilis.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val EmobilisPrimary    = Color(0xFF1565C0)
val EmobilisSecondary  = Color(0xFF0288D1)
val EmobilisAccent     = Color(0xFFFFA000)
val EmobilisBackground = Color(0xFFF5F5F5)

private val EmobilisColorScheme = lightColorScheme(
    primary          = EmobilisPrimary,
    secondary        = EmobilisSecondary,
    tertiary         = EmobilisAccent,
    background       = EmobilisBackground,
    surface          = Color.White,
    onPrimary        = Color.White,
    onSecondary      = Color.White,
    onBackground     = Color(0xFF1C1B1F),
    onSurface        = Color(0xFF1C1B1F),
)

@Composable
fun EmobilisTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = EmobilisColorScheme,
        typography  = Typography(),
        content     = content
    )
}

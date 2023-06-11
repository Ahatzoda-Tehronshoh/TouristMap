package com.tehronshoh.touristmap.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.colorResource
import com.tehronshoh.touristmap.R

@Composable
fun AppTheme(content: @Composable () -> Unit) {
    val DarkColorPalette = darkColors()
    val LightColorPalette = lightColors(
        primary = colorResource(id = R.color.primary)
    )

    val colors = if (isSystemInDarkTheme()) DarkColorPalette else LightColorPalette

    MaterialTheme(
        colors = colors,
        content = content
    )
}
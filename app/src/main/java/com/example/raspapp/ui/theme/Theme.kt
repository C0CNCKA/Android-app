package com.example.raspapp.ui.theme

import android.app.Activity
import android.os.Build
import android.view.WindowInsets
import androidx.activity.ComponentActivity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.compose.material3.Typography
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColors(
    primary = Color(0xFFBB86FC), // Основной цвет (фиолетовый оттенок)
    primaryVariant = Color(0xFF3700B3), // Вариант основного цвета
    secondary = Color(0xFF03DAC6), // Вторичный цвет (бирюзовый)
    background = Color(0xFF232323), // Тёмный фон
    surface = Color(0xFF5D5D5D), // Поверхности (чуть светлее фона)
    onPrimary = Color.Black, // Текст/иконки на основном цвете
    onSecondary = Color.Black, // Текст/иконки на вторичном цвете
    onBackground = Color.White, // Текст/иконки на фоне
    onSurface = Color.White // Текст/иконки на поверхностях
)

private val LightColorScheme = lightColors(
    primary = Color(0xFF6200EE), // Основной цвет (фиолетовый оттенок)
    primaryVariant = Color(0xFF3700B3), // Вариант основного цвета
    secondary = Color(0xFF03DAC6), // Вторичный цвет (бирюзовый)
    background = Color(0xFFFFFFFF), // Светлый фон
    surface = Color(0xFFF5F5F5), // Поверхности (чуть темнее фона)
    onPrimary = Color.White, // Текст/иконки на основном цвете
    onSecondary = Color.Black, // Текст/иконки на вторичном цвете
    onBackground = Color.Black, // Текст/иконки на фоне
    onSurface = Color.Black // Текст/иконки на поверхностях
)
@Composable
fun RaspAppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val colors: ColorScheme = when {
        (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) -> {
            if (useDarkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context)
        }
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    } as ColorScheme

    val customColorScheme = if (isSystemInDarkTheme()) colors.copy(
        surface = Color(0xFF343434),
        background = Color(0xFF1A1A1A),
       secondary = Color(0xFF402759)
    ) else colors.copy()

    MaterialTheme(
        colorScheme = customColorScheme,
        typography = Typography,
        content = content
    )
}

/*private val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    secondary = PurpleGrey80,
    tertiary = Pink80
)

private val LightColorScheme = lightColorScheme(
    primary = Purple40,
    secondary = PurpleGrey4Я0,
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

@Composable
fun RaspAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}*/
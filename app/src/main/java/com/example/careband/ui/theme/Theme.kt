package com.example.careband.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.careband.ui.theme.CareBandPink

val DarkColorScheme = darkColorScheme(
    primary = Purple80,
    onPrimary = Color.White,        // 버튼/상단바 위 텍스트
    secondary = PurpleGrey80,
    onSecondary = Color.White,
    background = Color.Black,
    surface = Color.DarkGray,
    onBackground = Color.White,     // 배경 위 텍스트
    onSurface = Color.White         // 카드나 표면 위 텍스트
)

val LightColorScheme = lightColorScheme(
    primary = CareBandPink,         // 상단바, 버튼 배경
    onPrimary = Color.Black,        // 상단바, 버튼 위 텍스트
    secondary = CareBandPink,
    onSecondary = Color.White,
    background = Color.White,       // 앱 배경
    surface = Color.White,          // 카드 등 기본 바탕
    onBackground = Color.Black,     // 배경 위 텍스트
    onSurface = Color.Black         // 카드, Surface 위 텍스트
)

@Composable
fun CareBandTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
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
        typography = Typography
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = colorScheme.background
        ) {
            content()
        }
    }
}

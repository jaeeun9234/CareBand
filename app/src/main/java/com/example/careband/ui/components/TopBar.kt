package com.example.careband.ui.components

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.example.careband.ui.theme.CareBandTheme // ✅ 우리가 정의한 테마 함수 import

@Composable
fun AppContent(content: @Composable () -> Unit) {
    CareBandTheme(
        darkTheme = isSystemInDarkTheme(),
        dynamicColor = false // 또는 true: 동적 색상 적용 여부
    ) {
        content() // 실제로 사용할 Composable 내용을 전달
    }
}

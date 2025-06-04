package com.example.careband.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.careband.data.model.UserType

@Composable
fun DrawerContent(
    isLoggedIn: Boolean,
    userType: UserType?,
    userName: String,
    onMenuClick: (String) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            if (isLoggedIn && userType != null && userName.isNotEmpty()) {
                // 사용자 정보 출력
                Text(
                    text = "$userName 님 [${userType.label}]",
                    style = MaterialTheme.typography.titleLarge
                )

                Spacer(modifier = Modifier.height(24.dp))

                // 메뉴 항목 정의
                val menuItems = when (userType) {
                    UserType.USER -> listOf(
                        "건강 기록", "의료 이력", "의료 리포트",
                        "데이터 시각화", "알림 기록", "기기 연결", "설정"
                    )
                    UserType.CAREGIVER -> listOf(
                        "의료 리포트", "알림 기록", "사용자 관리", "설정"
                    )
                }

                // 메뉴 출력
                menuItems.forEach { menu ->
                    Text(
                        text = "$menu →",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .clickable { onMenuClick(menu) },
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

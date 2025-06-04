package com.example.careband.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.careband.navigation.Route
import com.example.careband.data.model.UserType

@Composable
fun NavigationMenuScreen(
    navController: NavHostController,
    isLoggedIn: Boolean,
    userName: String?,
    userType: UserType?,
    onMenuClick: (String) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        if (isLoggedIn && userType != null && !userName.isNullOrEmpty()) {
            Text(
                text = "$userName 님 [${userType.label}]",
                modifier = Modifier.padding(bottom = 16.dp)
            )

            when (userType) {
                UserType.USER -> {
                    TextButton(onClick = { onMenuClick("건강 기록") }) {
                        Text(
                            text = "건강 기록",
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                    TextButton(onClick = { onMenuClick("의료 이력") }) {
                        Text(
                            text = "의료 이력",
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                    TextButton(onClick = { onMenuClick("의료 리포트") }) {
                        Text(
                            text = "의료 리포트",
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                    TextButton(onClick = { onMenuClick("데이터 시각화") }) {
                        Text(
                            text = "데이터 시각화",
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                    TextButton(onClick = { onMenuClick("알림 기록") }) {
                        Text(
                            text = "알림 기록",
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                    TextButton(onClick = { onMenuClick("기기 연결") }) {
                        Text(
                            text = "기기 연결",
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                    TextButton(onClick = { onMenuClick("설정") }) {
                        Text(
                            text = "설정",
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                }
                UserType.CAREGIVER -> {
                    TextButton(onClick = { onMenuClick("의료 리포트") }) {
                        Text(
                            text = "의료 리포트",
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                    TextButton(onClick = { onMenuClick("알림 기록") }) {
                        Text(
                            text = "알림 기록",
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                    TextButton(onClick = { onMenuClick("사용자 관리") }) {
                        Text(
                            text = "사용자 관리",
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                    TextButton(onClick = { onMenuClick("설정") }) {
                        Text(
                            text = "설정",
                            color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            TextButton(onClick = { navController.navigate(Route.PROFILE_MENU) }) {
                Text(
                    text = "계정 전환",
                    color = MaterialTheme.colorScheme.onSurface)
            }
        }
    }
}

package com.example.careband.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
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
                        Text("건강 기록", color = Color.Black)
                    }
                    TextButton(onClick = { onMenuClick("의료 이력") }) {
                        Text("의료 이력", color = Color.Black)
                    }
                    TextButton(onClick = { onMenuClick("의료 리포트") }) {
                        Text("의료 리포트", color = Color.Black)
                    }
                    TextButton(onClick = { onMenuClick("데이터 시각화") }) {
                        Text("데이터 시각화", color = Color.Black)
                    }
                    TextButton(onClick = { onMenuClick("알림 기록") }) {
                        Text("알림 기록", color = Color.Black)
                    }
                    TextButton(onClick = { onMenuClick("기기 연결") }) {
                        Text("기기 연결", color = Color.Black)
                    }
                    TextButton(onClick = { onMenuClick("설정") }) {
                        Text("설정", color = Color.Black)
                    }
                }
                UserType.CAREGIVER -> {
                    TextButton(onClick = { onMenuClick("의료 리포트") }) {
                        Text("의료 리포트", color = Color.Black)
                    }
                    TextButton(onClick = { onMenuClick("알림 기록") }) {
                        Text("알림 기록", color = Color.Black)
                    }
                    TextButton(onClick = { onMenuClick("사용자 관리") }) {
                        Text("사용자 관리", color = Color.Black)
                    }
                    TextButton(onClick = { onMenuClick("설정") }) {
                        Text("설정", color = Color.Black)
                    }
                }
            }

            TextButton(onClick = { navController.navigate(Route.PROFILE_MENU) }) {
                Text("계정 전환", color = Color.Black)
            }
        }
    }
}

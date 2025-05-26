package com.example.careband.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.careband.navigation.Route

@Composable
fun NavigationMenuScreen(navController: NavHostController) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "메뉴", modifier = Modifier.padding(bottom = 16.dp))

        TextButton(onClick = { navController.navigate(Route.HEALTH_RECORD) }) {
            Text("건강 기록")
        }
        TextButton(onClick = { navController.navigate(Route.MEDICAL_REPORT) }) {
            Text("의료 리포트")
        }
        TextButton(onClick = { navController.navigate(Route.ALERT_LOG) }) {
            Text("알림 기록")
        }
        TextButton(onClick = { navController.navigate(Route.USER_MANAGEMENT) }) {
            Text("사용자 관리")
        }
        TextButton(onClick = { navController.navigate(Route.PROFILE_MENU) }) {
            Text("계정 전환")
        }
        TextButton(onClick = { /* TODO: 설정 화면 구성 */ }) {
            Text("설정")
        }
    }
}

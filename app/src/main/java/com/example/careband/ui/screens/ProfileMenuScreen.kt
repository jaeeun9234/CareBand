package com.example.careband.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.careband.viewmodel.AuthViewModel
import com.example.careband.data.model.UserType

@Composable
fun ProfileMenuScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val userType by authViewModel.userType.collectAsState()
    val userName by authViewModel.userName.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "${userName ?: "이름 없음"} 님 [${userType?.label ?: "?"}]",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                // 계정 전환은 로그아웃 후 시작 화면으로 이동
                authViewModel.logout()
                navController.navigate("start") {
                    popUpTo("profile_menu") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("계정 전환")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                authViewModel.logout()
                navController.navigate("start") {
                    popUpTo("profile_menu") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
        ) {
            Text("로그아웃")
        }
    }
}

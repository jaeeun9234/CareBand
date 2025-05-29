package com.example.careband.ui.screens

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
import com.example.careband.ui.theme.CareBandPink

@Composable
fun ProfileMenuScreen(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val userType by authViewModel.userType.collectAsState()
    val userName by authViewModel.userName.collectAsState()

    val displayName = userName ?: "이름 없음"
    val typeLabel = when (userType) {
        UserType.USER -> "사용자"
        UserType.CAREGIVER -> "보호자"
        else -> "?"
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$displayName 님 [$typeLabel]",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = {
                authViewModel.logout()
                navController.navigate("login") {
                    popUpTo("profile_menu") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = CareBandPink)
        ) {
            Text("계정 전환", color = MaterialTheme.colorScheme.onPrimary)
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                authViewModel.logout()
                navController.navigate("login") {
                    popUpTo("profile_menu") { inclusive = true }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = CareBandPink)
        ) {
            Text("로그아웃", color = MaterialTheme.colorScheme.onPrimary)
        }
    }
}

package com.example.careband.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.careband.data.model.UserType
import com.example.careband.viewmodel.AuthViewModel

@Composable
fun ProfileMenuScreen(
    userName: String,
    userType: UserType,
    onSwitchAccount: () -> Unit,
    onLogout: () -> Unit
) {
    val authViewModel: AuthViewModel = viewModel()

    // ✅ 화면 진입 시 Firestore에서 사용자 정보 다시 불러오기
    LaunchedEffect(Unit) {
        authViewModel.loadUserInfoFromFirestore()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp, vertical = 64.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "[${if (userType == UserType.CAREGIVER) "보호자" else "사용자"}]",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "$userName 님",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(48.dp))

        Button(
            onClick = onSwitchAccount,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("계정 전환")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onLogout,
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("로그아웃")
        }
    }
}

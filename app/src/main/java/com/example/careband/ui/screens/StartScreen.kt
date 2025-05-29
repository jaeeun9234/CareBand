package com.example.careband.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun StartScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("CareBand", fontSize = 32.sp, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = onNavigateToLogin,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("로그인")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToRegister,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("회원가입")
        }
    }
}

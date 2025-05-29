package com.example.careband

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.careband.ui.theme.CareBandTheme

class LoginEntryActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CareBandTheme {
                LoginEntryScreen(
                    onLoginClick = {
                        // 로그인 → MainActivity에서 login screen으로 시작
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("startDestination", "login")
                        startActivity(intent)
                    },
                    onRegisterClick = {
                        // 회원가입 → MainActivity에서 register screen으로 시작
                        val intent = Intent(this, MainActivity::class.java)
                        intent.putExtra("startDestination", "register")
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun LoginEntryScreen(
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "CareBand에 오신 것을 환영합니다",
            fontSize = 24.sp,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(onClick = onLoginClick) {
            Text("로그인")
        }

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = onRegisterClick) {
            Text("회원가입")
        }
    }
}

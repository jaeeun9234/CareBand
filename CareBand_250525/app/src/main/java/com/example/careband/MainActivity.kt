package com.example.careband

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.careband.ui.screens.LoginScreen
import com.example.careband.ui.screens.RegisterScreen
import com.example.careband.ui.screens.HomeScreen
import com.example.careband.ui.theme.CareBandTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // LoginEntryActivity에서 전달한 startDestination 값 받기
        val startDestination = intent.getStringExtra("startDestination") ?: "login"

        setContent {
            CareBandTheme {
                val navController = rememberNavController()

                NavHost(navController, startDestination = startDestination) {
                    composable("login") {
                        LoginScreen(
                            onLoginSuccess = { navController.navigate("home") },
                            onNavigateToRegister = { navController.navigate("register") }
                        )
                    }
                    composable("register") {
                        RegisterScreen(
                            onRegisterSuccess = {
                                navController.navigate("home") {
                                    popUpTo("register") { inclusive = true } // 뒤로가기 시 회원가입으로 안 돌아가게
                                }
                            }
                        )
                    }

                    composable("home") {
                        HomeScreen(navController) // 홈 화면 Composable
                    }
                }
            }
        }
    }
}

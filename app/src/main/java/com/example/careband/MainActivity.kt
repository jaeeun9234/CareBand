package com.example.careband

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.careband.ui.components.CareBandTopBar
import com.example.careband.ui.components.DrawerContent
import com.example.careband.ui.screens.*
import com.example.careband.ui.theme.CareBandTheme
import com.example.careband.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CareBandTheme {
                val navController = rememberNavController()
                val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
                val scope = rememberCoroutineScope()

                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                val userType by authViewModel.userType.collectAsState()
                val userName by authViewModel.userName.collectAsState()

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        if (isLoggedIn && userType != null && userName != null) {
                            DrawerContent(
                                userType = userType!!,
                                userName = userName!!,
                                onMenuClick = { menuItem ->
                                    when (menuItem) {
                                        "건강 기록" -> navController.navigate("health_record")
                                        "의료 리포트" -> navController.navigate("medical_report")
                                        "알림 기록" -> navController.navigate("alert_log")
                                        "사용자 관리" -> navController.navigate("user_management")
                                        "계정 전환" -> navController.navigate("profile_menu")
                                        "설정" -> {} // 필요 시 추가
                                    }
                                    scope.launch { drawerState.close() }
                                }
                            )
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            CareBandTopBar(
                                isLoggedIn = isLoggedIn,
                                userType = userType,
                                onMenuClick = { scope.launch { drawerState.open() } },
                                onProfileClick = { navController.navigate("profile_menu") }
                            )
                        }
                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = "start",
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            composable("start") {
                                StartScreen(
                                    onNavigateToLogin = { navController.navigate("login") },
                                    onNavigateToRegister = { navController.navigate("register") }
                                )
                            }
                            composable("login") {
                                LoginScreen(
                                    onLoginSuccess = { navController.navigate("home") },
                                    onRegisterClick = { navController.navigate("register") }
                                )
                            }
                            composable("register") {
                                RegisterScreen(
                                    onRegisterSuccess = { navController.navigate("home") },
                                    onLoginClick = { navController.navigate("login") }
                                )
                            }
                            composable("home") { HomeScreen(navController) }
                            composable("profile_menu") { ProfileMenuScreen(navController) }
                            composable("health_record") { HealthRecordScreen(navController) }
                            // 필요 시 다른 화면 추가
                        }
                    }
                }
            }
        }
    }
}

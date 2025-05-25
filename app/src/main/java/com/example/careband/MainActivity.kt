package com.example.careband

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.example.careband.navigation.Route
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
                                        "건강 기록" -> navController.navigate(Route.HEALTH_RECORD)
                                        "의료 리포트" -> navController.navigate(Route.MEDICAL_REPORT)
                                        "알림 기록" -> navController.navigate(Route.ALERT_LOG)
                                        "사용자 관리" -> navController.navigate(Route.USER_MANAGEMENT)
                                        "계정 전환" -> navController.navigate(Route.PROFILE_MENU)
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
                                onProfileClick = { navController.navigate(Route.PROFILE_MENU) }
                            )
                        }
                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = Route.START,
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            composable(Route.START) {
                                StartScreen(
                                    onNavigateToLogin = { navController.navigate(Route.LOGIN) },
                                    onNavigateToRegister = { navController.navigate(Route.REGISTER) }
                                )
                            }
                            composable(Route.LOGIN) {
                                LoginScreen(
                                    onLoginSuccess = { navController.navigate(Route.HOME) },
                                    onRegisterClick = { navController.navigate(Route.REGISTER) }
                                )
                            }
                            composable(Route.REGISTER) {
                                RegisterScreen(
                                    onRegisterSuccess = { navController.navigate(Route.HOME) },
                                    onLoginClick = { navController.navigate(Route.LOGIN) }
                                )
                            }
                            composable(Route.HOME) { HomeScreen(navController) }
                            composable(Route.PROFILE_MENU) { ProfileMenuScreen(navController) }
                            composable(Route.HEALTH_RECORD) { HealthRecordScreen(navController) }

                            composable(Route.MEDICAL_REPORT) {
                                Text("의료 리포트 화면")
                            }

                            composable(Route.ALERT_LOG) {
                                Text("알림 기록 화면")
                            }

                            composable(Route.USER_MANAGEMENT) {
                                Text("사용자 관리 화면")
                            }
                        }
                    }
                }
            }
        }
    }
}
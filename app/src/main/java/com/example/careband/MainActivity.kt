package com.example.careband

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.*
import com.example.careband.navigation.Route
import com.example.careband.ui.components.CareBandTopBar
import com.example.careband.ui.screens.*
import com.example.careband.ui.theme.CareBandTheme
import com.example.careband.viewmodel.AuthViewModel

class MainActivity : ComponentActivity() {
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CareBandTheme {
                val navController = rememberNavController()
                val isLoggedIn by authViewModel.isLoggedIn.collectAsState()
                val userType by authViewModel.userType.collectAsState()
                val userName by authViewModel.userName.collectAsState()
                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStack?.destination?.route

                var startDestination by remember { mutableStateOf<String?>(null) }

                LaunchedEffect(isLoggedIn) {
                    startDestination = if (isLoggedIn) Route.HOME else Route.LOGIN
                }

                if (startDestination != null) {
                    Scaffold(
                        topBar = {
                            CareBandTopBar(
                                isLoggedIn = isLoggedIn,
                                userType = userType,
                                onMenuClick = {
                                    if (currentRoute == Route.NAV_MENU) {
                                        navController.popBackStack()
                                    } else {
                                        navController.navigate(Route.NAV_MENU)
                                    }
                                },
                                onProfileClick = {
                                    if (currentRoute == Route.PROFILE_MENU) {
                                        navController.popBackStack()
                                    } else {
                                        navController.navigate(Route.PROFILE_MENU)
                                    }
                                }
                            )
                        }
                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = startDestination!!,
                            modifier = Modifier.padding(paddingValues)
                        ) {
                            composable(Route.LOGIN) {
                                LoginScreen(
                                    onLoginSuccess = {
                                        navController.navigate(Route.HOME) {
                                            popUpTo(Route.LOGIN) { inclusive = true }
                                        }
                                    },
                                    onRegisterClick = {
                                        navController.navigate(Route.REGISTER)
                                    },
                                    drawerState = null,
                                    scope = rememberCoroutineScope(),
                                    authViewModel = authViewModel
                                )
                            }
                            composable(Route.REGISTER) {
                                RegisterScreen(
                                    onRegisterSuccess = {
                                        navController.navigate(Route.HOME) {
                                            popUpTo(Route.REGISTER) { inclusive = true }
                                        }
                                    },
                                    onLoginClick = {
                                        navController.navigate(Route.LOGIN)
                                    },
                                    drawerState = null,
                                    scope = rememberCoroutineScope(),
                                    authViewModel = authViewModel
                                )
                            }
                            composable(Route.HOME) {
                                HomeScreen(navController)
                            }
                            composable(Route.PROFILE_MENU) {
                                ProfileMenuScreen(navController)
                            }
                            composable(Route.HEALTH_RECORD) {
                                HealthRecordScreen(navController)
                            }
                            composable(Route.MEDICAL_REPORT) {
                                Text("의료 리포트 화면")
                            }
                            composable(Route.ALERT_LOG) {
                                Text("알림 기록 화면")
                            }
                            composable(Route.USER_MANAGEMENT) {
                                Text("사용자 관리 화면")
                            }
                            composable(Route.NAV_MENU) {
                                NavigationMenuScreen(
                                    navController = navController,
                                    userName = userName,
                                    userType = userType
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

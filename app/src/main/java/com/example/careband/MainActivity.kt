package com.example.careband

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.activity.compose.BackHandler
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

                // 로그인 상태에 따라 시작 화면 설정
                LaunchedEffect(isLoggedIn) {
                    startDestination = if (isLoggedIn) Route.HOME else Route.LOGIN

                    // 로그인된 상태에서 로그인 화면이 스택에 있다면 제거
                    if (isLoggedIn) {
                        navController.popBackStack(Route.LOGIN, inclusive = true)
                    }
                }

                // 🔐 ESC (뒤로가기) 차단 - 로그인된 상태에서 로그인 화면으로 이동한 경우 방지
                BackHandler(enabled = isLoggedIn && currentRoute == Route.LOGIN) {
                    // 아무 동작도 하지 않음 (ESC 무시)
                }

                if (startDestination != null) {
                    Scaffold(
                        topBar = {
                            CareBandTopBar(
                                isLoggedIn = isLoggedIn,
                                userType = userType,
                                userName = userName ?: "",
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
                            composable(Route.MEDICAL_HISTORY) {
                                MedicalHistoryScreen(navController)
                            }
                            composable(Route.DISEASE_RECORD) {
//                                DiseaseRecordScreen(
//                                    userId = authViewModel.userId.collectAsState().value ?: ""
//                                )
                                Text("질병 이력 화면")
                            }

                            composable(Route.MEDICATION_RECORD) {
//                                MedicationRecordScreen(
//                                    userId = authViewModel.userId.collectAsState().value ?: ""
//                                )
                                Text("복약 이력 화면")
                            }

                            composable(Route.VACCINATION_RECORD) {
//                                VaccinationRecordScreen(
//                                    userId = authViewModel.userId.collectAsState().value ?: ""
//                                )
                                Text("접종 이력 화면")
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
                                    isLoggedIn = isLoggedIn,
                                    userName = userName ?: "",
                                    userType = userType,
                                    onMenuClick = { menu ->
                                        when (menu) {
                                            "건강 기록" -> navController.navigate(Route.HEALTH_RECORD)
                                            "의료 이력" -> navController.navigate(Route.MEDICAL_HISTORY)
                                            "의료 리포트" -> navController.navigate(Route.MEDICAL_REPORT)
                                            "알림 기록" -> navController.navigate(Route.ALERT_LOG)
                                            "사용자 관리" -> navController.navigate(Route.USER_MANAGEMENT)
                                            "설정" -> { /* TODO */ }
                                            "기기 연결" -> { /* TODO */ }
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

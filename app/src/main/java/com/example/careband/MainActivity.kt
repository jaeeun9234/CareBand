package com.example.careband

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.*
import com.example.careband.navigation.Route
import com.example.careband.ui.components.CareBandTopBar
import com.example.careband.ui.components.DrawerContent
import com.example.careband.ui.screens.*
import com.example.careband.ui.theme.CareBandTheme
import com.example.careband.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
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

                val currentBackStack by navController.currentBackStackEntryAsState()
                val currentRoute = currentBackStack?.destination?.route

                // 아이콘 표시 조건
                val showIcons = isLoggedIn && currentRoute == Route.HOME

                // 시작 화면 설정 및 홈으로 강제 이동
                var startDestination by remember { mutableStateOf(Route.LOGIN) }
                LaunchedEffect(isLoggedIn) {
                    startDestination = if (isLoggedIn) Route.HOME else Route.LOGIN
                    if (isLoggedIn) {
                        navController.navigate(Route.HOME) {
                            popUpTo(0)
                        }
                    }
                }

                // Drawer 열릴 때마다 사용자 정보 갱신
                LaunchedEffect(drawerState.isOpen) {
                    if (drawerState.isOpen) {
                        val uid = FirebaseAuth.getInstance().currentUser?.uid
                        if (uid != null) {
                            authViewModel.loadUserData(uid)
                        }
                    }
                }

                ModalNavigationDrawer(
                    drawerState = drawerState,
                    drawerContent = {
                        if (showIcons && userType != null && userName != null) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                color = Color.White // ← Drawer 배경 흰색
                            ) {
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
                                            "설정" -> {}
                                        }
                                        scope.launch { drawerState.close() }
                                    }
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.padding(0.dp))
                        }
                    }
                ) {
                    Scaffold(
                        topBar = {
                            CareBandTopBar(
                                isLoggedIn = isLoggedIn,
                                userType = userType,
                                onMenuClick = { scope.launch { drawerState.open() } },
                                onProfileClick = {
                                    val current = navController.currentDestination?.route
                                    if (current == Route.PROFILE_MENU) {
                                        navController.popBackStack() // ← 현재가 PROFILE이면 되돌아감
                                    } else {
                                        navController.navigate(Route.PROFILE_MENU)
                                    }
                                }
                            )
                        }
                    ) { paddingValues ->
                        NavHost(
                            navController = navController,
                            startDestination = startDestination,
                            modifier = Modifier.padding(paddingValues)
                        ) {
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
                        }
                    }
                }
            }
        }
    }
}

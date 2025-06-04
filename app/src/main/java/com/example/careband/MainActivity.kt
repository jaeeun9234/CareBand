package com.example.careband

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.navigation.compose.*
import com.example.careband.ble.BleManager
import com.example.careband.navigation.Route
import com.example.careband.ui.components.CareBandTopBar
import com.example.careband.ui.screens.*
import com.example.careband.ui.theme.CareBandTheme
import com.example.careband.viewmodel.AuthViewModel
import com.example.careband.ui.screens.VitalSignsChartScreen
import com.example.careband.viewmodel.MedicationCheckViewModel
import com.example.careband.viewmodel.SensorDataViewModel

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
                    if (isLoggedIn) {
                        navController.popBackStack(Route.LOGIN, inclusive = true)
                    }
                }

                BackHandler(enabled = isLoggedIn && currentRoute == Route.LOGIN) {
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
                                DiseaseRecordScreen(
                                    userId = authViewModel.userId.collectAsState().value ?: ""
                                )
                            }
                            composable(Route.MEDICATION_RECORD) {
                                MedicationRecordScreen(
                                    userId = authViewModel.userId.collectAsState().value ?: ""
                                )
                            }
                            composable(Route.VACCINATION_RECORD) {
                                VaccinationRecordScreen(
                                    userId = authViewModel.userId.collectAsState().value ?: ""
                                )
                            }
                            composable(Route.MEDICAL_REPORT) {
                                Text("의료 리포트 화면")
                            }
                            composable(Route.VITALSIGNS_VIEW){
                                VitalSignsChartScreen(
                                    userId = authViewModel.userId.collectAsState().value ?: ""
                                )
                            }
                            composable(Route.ALERT_LOG) {
                                Text("알림 기록 화면")
                            }
                            composable(Route.USER_MANAGEMENT) {
                                Text("사용자 관리 화면")
                            }
                            composable(Route.DEVICE_CONNECTION) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                    val context = LocalContext.current
                                    val hasPermissions =
                                        ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                                                ActivityCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED

                                    if (hasPermissions) {
                                        DeviceConnectionScreen(
                                            userId = authViewModel.userId.collectAsState().value ?: ""
                                        )
                                    } else {
                                        Text("BLE 권한이 필요합니다. 설정에서 권한을 허용하세요.")
                                    }
                                } else {
                                    Text("BLE 연결은 Android 12(API 31)+ 이상에서만 지원됩니다.")
                                }
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
                                            "데이터 시각화" -> navController.navigate(Route.VITALSIGNS_VIEW)
                                            "알림 기록" -> navController.navigate(Route.ALERT_LOG)
                                            "사용자 관리" -> navController.navigate(Route.USER_MANAGEMENT)
                                            "설정" -> { /* TODO */ }
                                            "기기 연결" -> navController.navigate(Route.DEVICE_CONNECTION)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 1001) {
            if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Log.d("BLE", "✅ 권한 허용됨")
            } else {
                Log.w("BLE", "❌ 권한 거부됨")
            }
        }
    }
}

package com.example.careband.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.careband.navigation.Route
import com.example.careband.viewmodel.AuthViewModel
import com.example.careband.viewmodel.MedicationCheckViewModel
import com.example.careband.data.model.UserType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(navController: NavController) {
    val authViewModel: AuthViewModel = viewModel()
    val medicationCheckViewModel: MedicationCheckViewModel = viewModel()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    val userType by authViewModel.userType.collectAsState()
    val userId = authViewModel.userId.collectAsState().value ?: ""
    val today = remember { SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date()) }
    val todayMedications by medicationCheckViewModel.todayMedications.collectAsState()

    // 로그인 상태 유지 확인
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                authViewModel.checkLoginStatus()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    // 뒤로 가기 시 로그아웃
    BackHandler(enabled = true) {
        authViewModel.logout()
        navController.navigate(Route.LOGIN) {
            popUpTo(Route.HOME) { inclusive = true }
        }
    }

    // 데이터 로딩
    LaunchedEffect(userId) {
        if (userId.isNotBlank()) {
            medicationCheckViewModel.loadTodayMedications(userId)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 날짜 표시
        Text(
            text = today,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(8.dp)
                .background(Color(0xFFFADADD), RoundedCornerShape(8.dp))
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 생체 정보 (임시 비움)
        val heartRate: String? = null
        val bloodPressure: String? = null

        if (heartRate != null || bloodPressure != null) {
            // 향후 생체 정보 시각화 추가
        } else {
            Text("등록된 생체 정보가 없습니다.", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 복약 정보
        if (todayMedications.isNotEmpty()) {
            todayMedications.forEach { record ->
                MedicationItem(
                    name = record.medicineName,
                    startDate = record.startDate,
                    endDate = record.endDate,
                    checked = record.takenDates.contains(today),
                    onChecked = { isChecked ->
                        medicationCheckViewModel.updateMedicationCheckState(
                            userId = userId,
                            record = record,
                            isChecked = isChecked
                        )
                    }
                )
            }
        } else {
            Text("등록된 약 정보가 없습니다.", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 사용자 유형별 버튼 구성
        userType?.let { type ->
            when (type) {
                UserType.USER -> {
                    HomeButtonRow(
                        leftLabel = "의료 리포트",
                        leftRoute = Route.MEDICAL_REPORT,
                        rightLabel = "알림 기록",
                        rightRoute = Route.ALERT_LOG,
                        navController = navController
                    )
                    HomeButtonRow(
                        leftLabel = "건강 기록",
                        leftRoute = Route.HEALTH_RECORD,
                        rightLabel = "의료 이력",
                        rightRoute = Route.MEDICAL_HISTORY,
                        navController = navController
                    )
                }
                UserType.CAREGIVER -> {
                    HomeButtonRow(
                        leftLabel = "의료 리포트",
                        leftRoute = Route.MEDICAL_REPORT,
                        rightLabel = "알림 기록",
                        rightRoute = Route.ALERT_LOG,
                        navController = navController
                    )
                    HomeButtonRow(
                        leftLabel = "사용자 관리",
                        leftRoute = Route.USER_MANAGEMENT,
                        rightLabel = "의료 이력",
                        rightRoute = Route.MEDICAL_HISTORY,
                        navController = navController
                    )
                }
            }
        }
    }
}

@Composable
fun MedicationItem(
    name: String,
    startDate: String,
    endDate: String,
    checked: Boolean,
    onChecked: (Boolean) -> Unit
) {
    var isChecked by remember { mutableStateOf(checked) }
    val period = if (endDate.isNotBlank()) "$startDate ~ $endDate" else "$startDate ~"

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFFF3F3F3), shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = period, fontSize = 14.sp, color = Color.Gray)
        }

        Checkbox(
            checked = isChecked,
            onCheckedChange = {
                isChecked = it
                onChecked(it)
            }
        )
    }
}

@Composable
fun HomeButtonRow(
    leftLabel: String,
    leftRoute: String,
    rightLabel: String,
    rightRoute: String,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        HomeButton(
            label = leftLabel,
            onClick = { navController.navigate(leftRoute) },
            modifier = Modifier.weight(1f)
        )
        HomeButton(
            label = rightLabel,
            onClick = { navController.navigate(rightRoute) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun HomeButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier
            .height(48.dp)
    ) {
        Text(text = label, textAlign = TextAlign.Center)
    }
}

package com.example.careband.ui.screens

import android.app.Activity
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.careband.R
import com.example.careband.data.model.UserType
import com.example.careband.navigation.Route
import com.example.careband.viewmodel.AuthViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(navController: NavController) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val authViewModel: AuthViewModel = viewModel()

    val userType by authViewModel.userType.collectAsState()

    // 로그인 상태 유지 확인 (화면 다시 올 때마다 체크)
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

    BackHandler(enabled = true) {
        // 뒤로가기 시 명시적 로그아웃 처리
        authViewModel.logout()
        navController.navigate(Route.LOGIN) {
            popUpTo(Route.HOME) { inclusive = true }
        }
    }

    val today = remember {
        SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
    }

    val heartRate: String? = null
    val bloodPressure: String? = null
    val medicationList: List<Triple<String, String, Boolean>> = emptyList()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
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

        if (heartRate != null || bloodPressure != null) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                heartRate?.let {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = painterResource(id = android.R.drawable.ic_menu_info_details), contentDescription = null)
                        Text(it, fontSize = 20.sp)
                    }
                }
                bloodPressure?.let {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Image(painter = painterResource(id = android.R.drawable.ic_menu_info_details), contentDescription = null)
                        Text(it, fontSize = 20.sp)
                    }
                }
            }
        } else {
            Text("등록된 생체 정보가 없습니다.", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (medicationList.isNotEmpty()) {
            medicationList.forEach { (name, time, checked) ->
                MedicationItem(name, time, checked)
            }
        } else {
            Text("등록된 약 정보가 없습니다.", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            HomeButton("의료 리포트", onClick = { navController.navigate(Route.MEDICAL_REPORT) }, modifier = Modifier.weight(1f))
//            HomeButton("알림 기록", onClick = { navController.navigate(Route.ALERT_LOG) }, modifier = Modifier.weight(1f))
//        }
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            HomeButton("건강 기록", onClick = { navController.navigate(Route.HEALTH_RECORD) }, modifier = Modifier.weight(1f))
//            HomeButton("의료 이력", onClick = { navController.navigate(Route.MEDICAL_HISTORY) }, modifier = Modifier.weight(1f))
//        }

        userType?.let { type ->
            when (type) {
                UserType.USER -> {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        HomeButton("의료 리포트", onClick = { navController.navigate(Route.MEDICAL_REPORT) }, modifier = Modifier.weight(1f))
                        HomeButton("알림 기록", onClick = { navController.navigate(Route.ALERT_LOG) }, modifier = Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        HomeButton("건강 기록", onClick = { navController.navigate(Route.HEALTH_RECORD) }, modifier = Modifier.weight(1f))
                        HomeButton("의료 이력", onClick = { navController.navigate(Route.MEDICAL_HISTORY) }, modifier = Modifier.weight(1f))
                    }
                }
                UserType.CAREGIVER -> {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        HomeButton("의료 리포트", onClick = { navController.navigate(Route.MEDICAL_REPORT) }, modifier = Modifier.weight(1f))
                        HomeButton("알림 기록", onClick = { navController.navigate(Route.ALERT_LOG) }, modifier = Modifier.weight(1f))
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                        HomeButton("사용자 관리", onClick = { navController.navigate(Route.USER_MANAGEMENT) }, modifier = Modifier.weight(1f))
                        HomeButton("의료 이력", onClick = { navController.navigate(Route.MEDICAL_HISTORY) }, modifier = Modifier.weight(1f))
                    }
                }
            }
        }

    }
}

@Composable
fun MedicationItem(name: String, time: String, checked: Boolean) {
    var isChecked by remember { mutableStateOf(checked) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFFF3F3F3), shape = RoundedCornerShape(8.dp))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = android.R.drawable.ic_menu_info_details),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(text = time, fontSize = 14.sp)
        }
        Checkbox(checked = isChecked, onCheckedChange = { isChecked = it })
    }
}

@Composable
fun HomeButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.padding(4.dp)
    ) {
        Text(text = label, textAlign = TextAlign.Center)
    }
}

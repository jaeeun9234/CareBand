package com.example.careband.ui.screens

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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.careband.R
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun HomeScreen(navController: NavController) {
    val today = remember {
        SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())
    }

    // 샘플 데이터 - 실제 앱에서는 ViewModel이나 DB에서 받아와야 함
    val heartRate: String? = null // ex: "89 BPM"
    val bloodPressure: String? = null // ex: "120/80 mmHg"
    val medicationList: List<Triple<String, String, Boolean>> = emptyList() // 약이 없으면 빈 리스트

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 날짜
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

        // BPM & 혈압 (값이 있을 때만 표시)
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

        // 약 리스트
        if (medicationList.isNotEmpty()) {
            medicationList.forEach { (name, time, checked) ->
                MedicationItem(name, time, checked)
            }
        } else {
            Text("등록된 약 정보가 없습니다.", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 하단 버튼은 항상 표시
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HomeButton("의료 리포트", onClick = { navController.navigate("report") }, modifier = Modifier.weight(1f))
            HomeButton("알림 기록", onClick = { navController.navigate("alerts") }, modifier = Modifier.weight(1f))
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            HomeButton("건강 기록", onClick = { navController.navigate("health") }, modifier = Modifier.weight(1f))
            HomeButton("질병 기록", onClick = { navController.navigate("disease") }, modifier = Modifier.weight(1f))
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

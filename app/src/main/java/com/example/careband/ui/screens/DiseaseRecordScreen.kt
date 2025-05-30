package com.example.careband.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.careband.data.model.DiseaseRecord
import com.example.careband.viewmodel.DiseaseViewModel
import com.example.careband.viewmodel.DiseaseViewModelFactory
import java.util.*

@Composable
fun DiseaseRecordScreen(
    userId: String
) {
    // ✅ userId가 유효하지 않으면 로딩 메시지만 표시
    if (userId.isBlank()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("사용자 정보를 불러오는 중...")
        }
        return
    }

    // ✅ userId가 준비된 경우에만 ViewModel 생성
    val viewModel: DiseaseViewModel = viewModel(factory = DiseaseViewModelFactory(userId))
    val records by viewModel.diseaseRecords.collectAsState()

    LaunchedEffect(Unit) {
        println("📌 DiseaseRecordScreen에 전달된 userId: $userId")
    }

    // 입력 상태
    var diseaseName by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var treatment by remember { mutableStateOf("") }
    var doctor by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text("질병 이력", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(16.dp))

        // 진단일
        OutlinedTextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("진단일") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(context, { _, year, month, day ->
                        startDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                }) {
                    Icon(Icons.Default.Add, contentDescription = "진단일 선택")
                }
            }
        )

        // 종료일
        OutlinedTextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text("종료일 (선택)") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            trailingIcon = {
                IconButton(onClick = {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(context, { _, year, month, day ->
                        endDate = String.format("%04d-%02d-%02d", year, month + 1, day)
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                }) {
                    Icon(Icons.Default.Add, contentDescription = "종료일 선택")
                }
            }
        )

        OutlinedTextField(
            value = diseaseName,
            onValueChange = { diseaseName = it },
            label = { Text("질병명") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
        )

        OutlinedTextField(
            value = treatment,
            onValueChange = { treatment = it },
            label = { Text("치료 내용") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        OutlinedTextField(
            value = doctor,
            onValueChange = { doctor = it },
            label = { Text("담당의") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        OutlinedTextField(
            value = memo,
            onValueChange = { memo = it },
            label = { Text("메모") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            onClick = {
                if (startDate.isNotBlank() && diseaseName.isNotBlank()) {
                    val newRecord = DiseaseRecord(
                        diagnosedDate = startDate,
                        endDate = endDate.takeIf { it.isNotBlank() },
                        diseaseName = diseaseName,
                        treatment = treatment,
                        doctor = doctor,
                        memo = memo,
                        userId = userId
                    )
                    viewModel.addDiseaseRecord(newRecord)
                    diseaseName = ""; startDate = ""; endDate = ""
                    treatment = ""; doctor = ""; memo = ""
                }
            },
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 8.dp)
        ) {
            Text("추가")
        }

        Spacer(modifier = Modifier.height(24.dp))
        Divider()
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(records.withIndex().toList()) { (index, record) ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("${index + 1}. ${record.diseaseName}", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = if (record.endDate != null)
                                "${record.diagnosedDate} ~ ${record.endDate}"
                            else
                                "${record.diagnosedDate} ~",
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("치료 내용", style = MaterialTheme.typography.labelSmall)
                        Text(record.treatment.ifBlank { "없음" })

                        Spacer(modifier = Modifier.height(4.dp))
                        Text("담당의", style = MaterialTheme.typography.labelSmall)
                        Text(record.doctor.ifBlank { "없음" })

                        if (record.memo.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("메모", style = MaterialTheme.typography.labelSmall)
                            Text(record.memo)
                        }
                    }
                }
            }
        }
    }
}

package com.example.careband.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.careband.data.model.MedicationRecord
import com.example.careband.viewmodel.MedicationViewModel
import com.example.careband.viewmodel.MedicationViewModelFactory
import java.util.*

@Composable
fun MedicationRecordScreen(userId: String) {
    if (userId.isBlank()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("사용자 정보를 불러오는 중...")
        }
        return
    }

    val viewModel: MedicationViewModel = viewModel(factory = MedicationViewModelFactory(userId))
    val records by viewModel.medicationRecords.collectAsState()

    // 상태값
    var editingRecord: MedicationRecord? by remember { mutableStateOf(null) }
    var medicineName by remember { mutableStateOf("") }
    var dosage by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(modifier = Modifier
        .fillMaxSize()
        .padding(16.dp)) {

        Text(
            text = if (editingRecord == null) "새 복약 기록 입력" else "복약 기록 수정 중",
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = medicineName,
            onValueChange = { medicineName = it },
            label = { Text("약 이름") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = dosage,
            onValueChange = { dosage = it },
            label = { Text("복용량") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = frequency,
            onValueChange = { frequency = it },
            label = { Text("복용 빈도") },
            modifier = Modifier.fillMaxWidth()
        )

        // 시작일 선택
        OutlinedTextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("복용 시작일") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(context, { _, year, month, day ->
                        startDate = "%04d-%02d-%02d".format(year, month + 1, day)
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "날짜 선택")
                }
            }
        )

        // 종료일 선택
        OutlinedTextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text("복용 종료일 (선택)") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
            trailingIcon = {
                IconButton(onClick = {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(context, { _, year, month, day ->
                        endDate = "%04d-%02d-%02d".format(year, month + 1, day)
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
                }) {
                    Icon(Icons.Default.Edit, contentDescription = "날짜 선택")
                }
            }
        )

        OutlinedTextField(
            value = memo,
            onValueChange = { memo = it },
            label = { Text("메모") },
            modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(onClick = {
            if (medicineName.isNotBlank() && startDate.isNotBlank()) {
                val recordId = editingRecord?.id
                    ?: "medicationRecord:$userId:$medicineName:$startDate"

                val newRecord = MedicationRecord(
                    id = recordId,
                    medicineName = medicineName,
                    dosage = dosage,
                    frequency = frequency,
                    startDate = startDate,
                    endDate = endDate,
                    memo = memo,
                    userId = userId
                )

                if (editingRecord == null) {
                    viewModel.addMedicationRecord(newRecord)
                } else {
                    viewModel.updateMedicationRecord(newRecord)
                    editingRecord = null
                }

                medicineName = ""; dosage = ""; frequency = ""
                startDate = ""; endDate = ""; memo = ""
            }
        }) {
            Text(if (editingRecord == null) "추가" else "수정 완료")
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${index + 1}. ${record.medicineName}",
                                style = MaterialTheme.typography.titleMedium,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(onClick = {
                                editingRecord = record
                                medicineName = record.medicineName
                                dosage = record.dosage
                                frequency = record.frequency
                                startDate = record.startDate
                                endDate = record.endDate
                                memo = record.memo
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "수정")
                            }

                            IconButton(onClick = {
                                viewModel.deleteMedicationRecord(record.id)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "삭제")
                            }
                        }

                        Text("${record.startDate} ~ ${record.endDate.ifBlank { " " }}")
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("복용량: ${record.dosage}")
                        Text("복용 빈도: ${record.frequency}")
                        if (record.memo.isNotBlank()) {
                            Text("메모: ${record.memo}")
                        }
                    }
                }
            }
        }
    }
}

package com.example.careband.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.careband.data.model.VaccinationRecord
import com.example.careband.viewmodel.VaccinationViewModel
import com.example.careband.viewmodel.VaccinationViewModelFactory
import java.util.*

@Composable
fun VaccinationRecordScreen(userId: String) {
    if (userId.isBlank()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("사용자 정보를 불러오는 중...")
        }
        return
    }

    val viewModel: VaccinationViewModel = viewModel(factory = VaccinationViewModelFactory(userId))
    val records by viewModel.vaccinationRecords.collectAsState()

    var editingRecord: VaccinationRecord? by remember { mutableStateOf(null) }
    var vaccineName by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var memo by remember { mutableStateOf("") }

    val context = LocalContext.current

    Column(Modifier.padding(16.dp)) {
        Text(
            if (editingRecord == null) "백신 접종 기록 입력" else "백신 접종 기록 수정 중",
            style = MaterialTheme.typography.titleMedium
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = vaccineName,
            onValueChange = { vaccineName = it },
            label = { Text("백신 이름") },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = date,
            onValueChange = { date = it },
            label = { Text("접종일") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    val calendar = Calendar.getInstance()
                    DatePickerDialog(context, { _, y, m, d ->
                        date = "%04d-%02d-%02d".format(y, m + 1, d)
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
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(Modifier.height(12.dp))

        Button(onClick = {
            if (vaccineName.isNotBlank() && date.isNotBlank()) {
                val id = editingRecord?.id ?: "vaccination:$userId:$vaccineName:$date"
                val record = VaccinationRecord(id, vaccineName, date, memo, userId)

                if (editingRecord == null) viewModel.addVaccinationRecord(record)
                else {
                    viewModel.updateVaccinationRecord(record)
                    editingRecord = null
                }

                vaccineName = ""; date = ""; memo = ""
            }
        }) {
            Text(if (editingRecord == null) "추가" else "수정 완료")
        }

        Spacer(Modifier.height(24.dp))
        Divider()
        Spacer(Modifier.height(8.dp))

        LazyColumn {
            items(records) { record ->
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(record.vaccineName, modifier = Modifier.weight(1f))

                            IconButton(onClick = {
                                editingRecord = record
                                vaccineName = record.vaccineName
                                date = record.date
                                memo = record.memo
                            }) {
                                Icon(Icons.Default.Edit, contentDescription = "수정")
                            }

                            IconButton(onClick = {
                                viewModel.deleteVaccinationRecord(record.id)
                            }) {
                                Icon(Icons.Default.Delete, contentDescription = "삭제")
                            }
                        }

                        Text("접종일: ${record.date}")
                        if (record.memo.isNotBlank()) {
                            Text("메모: ${record.memo}")
                        }
                    }
                }
            }
        }
    }
}

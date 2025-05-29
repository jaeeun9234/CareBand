//package com.example.careband.ui.screens
//
//import android.app.DatePickerDialog
//import androidx.compose.foundation.layout.*
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.ui.text.input.KeyboardOptions
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.Add
//import androidx.compose.material3.*
//import androidx.compose.runtime.*
//import androidx.compose.ui.Alignment
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.viewmodel.compose.viewModel
//import com.example.careband.data.model.MedicationRecord
//import com.example.careband.viewmodel.MedicalHistoryViewModel
//import com.example.careband.viewmodel.MedicalHistoryViewModelFactory
//import java.util.*
//
//@Composable
//fun MedicationRecordScreen(
//    userId: String,
//    viewModel: MedicalHistoryViewModel = viewModel(factory = MedicalHistoryViewModelFactory(userId))
//) {
//    val records by viewModel.medicationRecords.collectAsState()
//    var medicineName by remember { mutableStateOf("") }
//    var dosage by remember { mutableStateOf("") }
//    var selectedDate by remember { mutableStateOf("") }
//
//    val context = LocalContext.current
//
//    Column(modifier = Modifier.padding(16.dp)) {
//        Text("복약 이력", style = MaterialTheme.typography.headlineSmall)
//        Spacer(modifier = Modifier.height(16.dp))
//
//        // 날짜 선택
//        OutlinedTextField(
//            value = selectedDate,
//            onValueChange = { selectedDate = it },
//            label = { Text("복용 날짜") },
//            readOnly = true,
//            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
//            trailingIcon = {
//                IconButton(onClick = {
//                    val calendar = Calendar.getInstance()
//                    DatePickerDialog(
//                        context,
//                        { _, year, month, day ->
//                            selectedDate = String.format("%04d-%02d-%02d", year, month + 1, day)
//                        },
//                        calendar.get(Calendar.YEAR),
//                        calendar.get(Calendar.MONTH),
//                        calendar.get(Calendar.DAY_OF_MONTH)
//                    ).show()
//                }) {
//                    Icon(Icons.Default.Add, contentDescription = "날짜 선택")
//                }
//            }
//        )
//
//        // 약물명 입력
//        OutlinedTextField(
//            value = medicineName,
//            onValueChange = { medicineName = it },
//            label = { Text("약물명") },
//            modifier = Modifier.fillMaxWidth(),
//            keyboardOptions = KeyboardOptions.Default
//        )
//
//        Spacer(modifier = Modifier.height(8.dp))
//
//        // 용량 입력
//        OutlinedTextField(
//            value = dosage,
//            onValueChange = { dosage = it },
//            label = { Text("용량 (예: 500mg)") },
//            modifier = Modifier.fillMaxWidth(),
//            keyboardOptions = KeyboardOptions.Default
//        )
//
//        Spacer(modifier = Modifier.height(12.dp))
//
//        Button(
//            onClick = {
//                if (selectedDate.isNotBlank() && medicineName.isNotBlank() && dosage.isNotBlank()) {
//                    val newRecord = MedicationRecord(
//                        date = selectedDate,
//                        medicineName = medicineName,
//                        dosage = dosage
//                    )
//                    viewModel.insertMedicationRecord(newRecord)
//                    selectedDate = ""
//                    medicineName = ""
//                    dosage = ""
//                }
//            },
//            modifier = Modifier.align(Alignment.End)
//        ) {
//            Text("➕ 추가")
//        }
//
//        Spacer(modifier = Modifier.height(24.dp))
//        Divider()
//        Spacer(modifier = Modifier.height(8.dp))
//
//        Text("기록 목록", style = MaterialTheme.typography.titleMedium)
//
//        LazyColumn(modifier = Modifier.fillMaxSize()) {
//            items(records) { record ->
//                Text("• ${record.medicineName} (${record.date}) - ${record.dosage}", modifier = Modifier.padding(vertical = 4.dp))
//                // TODO: 수정 기능 추가 가능
//            }
//        }
//    }
//}

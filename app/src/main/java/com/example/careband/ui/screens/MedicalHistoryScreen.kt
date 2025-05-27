package com.example.careband.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.careband.viewmodel.AuthViewModel
import com.example.careband.viewmodel.MedicalHistoryViewModel
import com.example.careband.viewmodel.MedicalHistoryViewModelFactory
import com.example.careband.data.model.*

@Composable
fun MedicalHistoryScreen() {
    val authViewModel: AuthViewModel = viewModel()
    val userId by authViewModel.userId.collectAsState()

    val viewModel: MedicalHistoryViewModel = viewModel(
        factory = MedicalHistoryViewModelFactory(userId ?: "")
    )

    val vaccinations by viewModel.vaccinationRecords.collectAsState()
    val medications by viewModel.medicationRecords.collectAsState()
    val diseases by viewModel.diseaseRecords.collectAsState()

    var vaccineName by remember { mutableStateOf(TextFieldValue("")) }
    var vaccineDate by remember { mutableStateOf(TextFieldValue("")) }

    var medicineName by remember { mutableStateOf(TextFieldValue("")) }
    var startDate by remember { mutableStateOf(TextFieldValue("")) }
    var endDate by remember { mutableStateOf(TextFieldValue("")) }

    var diseaseName by remember { mutableStateOf(TextFieldValue("")) }
    var diagnosedDate by remember { mutableStateOf(TextFieldValue("")) }

    var editingVaccine: VaccinationRecord? by remember { mutableStateOf(null) }
    var editingMedication: MedicationRecord? by remember { mutableStateOf(null) }
    var editingDisease: DiseaseRecord? by remember { mutableStateOf(null) }

    LaunchedEffect(Unit) {
        viewModel.loadAllRecords()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        SectionTitle("접종 기록")
        OutlinedTextField(
            value = vaccineName,
            onValueChange = { vaccineName = it },
            label = { Text("백신 이름") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = vaccineDate,
            onValueChange = { vaccineDate = it },
            label = { Text("접종 날짜 (예: 2024-05-27)") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        Button(
            onClick = {
                if (vaccineName.text.isNotBlank() && vaccineDate.text.isNotBlank()) {
                    if (editingVaccine == null) {
                        viewModel.addVaccinationRecord(
                            VaccinationRecord(
                                vaccineName = vaccineName.text,
                                date = vaccineDate.text,
                                userId = userId ?: ""
                            )
                        )
                    } else {
                        viewModel.updateVaccinationRecord(
                            editingVaccine!!.copy(
                                vaccineName = vaccineName.text,
                                date = vaccineDate.text
                            )
                        )
                        editingVaccine = null
                    }
                    vaccineName = TextFieldValue("")
                    vaccineDate = TextFieldValue("")
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("저장")
        }
        vaccinations.forEach {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("- ${it.vaccineName} (${it.date})", modifier = Modifier.padding(bottom = 4.dp))
                Text("수정", color = MaterialTheme.colorScheme.primary, modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable {
                        editingVaccine = it
                        vaccineName = TextFieldValue(it.vaccineName)
                        vaccineDate = TextFieldValue(it.date)
                    })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("복약 기록")
        OutlinedTextField(
            value = medicineName,
            onValueChange = { medicineName = it },
            label = { Text("약 이름") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = startDate,
            onValueChange = { startDate = it },
            label = { Text("복용 시작일") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = endDate,
            onValueChange = { endDate = it },
            label = { Text("복용 종료일 (비워도 가능)") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        Button(
            onClick = {
                if (medicineName.text.isNotBlank() && startDate.text.isNotBlank()) {
                    if (editingMedication == null) {
                        viewModel.addMedicationRecord(
                            MedicationRecord(
                                medicineName = medicineName.text,
                                startDate = startDate.text,
                                endDate = endDate.text,
                                userId = userId ?: ""
                            )
                        )
                    } else {
                        viewModel.updateMedicationRecord(
                            editingMedication!!.copy(
                                medicineName = medicineName.text,
                                startDate = startDate.text,
                                endDate = endDate.text
                            )
                        )
                        editingMedication = null
                    }
                    medicineName = TextFieldValue("")
                    startDate = TextFieldValue("")
                    endDate = TextFieldValue("")
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("저장")
        }
        medications.forEach {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("- ${it.medicineName} (${it.startDate} ~ ${it.endDate.ifBlank { "진행 중" }})", modifier = Modifier.padding(bottom = 4.dp))
                Text("수정", color = MaterialTheme.colorScheme.primary, modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable {
                        editingMedication = it
                        medicineName = TextFieldValue(it.medicineName)
                        startDate = TextFieldValue(it.startDate)
                        endDate = TextFieldValue(it.endDate)
                    })
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("질병 기록")
        OutlinedTextField(
            value = diseaseName,
            onValueChange = { diseaseName = it },
            label = { Text("질병 이름") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        OutlinedTextField(
            value = diagnosedDate,
            onValueChange = { diagnosedDate = it },
            label = { Text("진단 날짜") },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
        )
        Button(
            onClick = {
                if (diseaseName.text.isNotBlank() && diagnosedDate.text.isNotBlank()) {
                    if (editingDisease == null) {
                        viewModel.addDiseaseRecord(
                            DiseaseRecord(
                                diseaseName = diseaseName.text,
                                diagnosedDate = diagnosedDate.text,
                                userId = userId ?: ""
                            )
                        )
                    } else {
                        viewModel.updateDiseaseRecord(
                            editingDisease!!.copy(
                                diseaseName = diseaseName.text,
                                diagnosedDate = diagnosedDate.text
                            )
                        )
                        editingDisease = null
                    }
                    diseaseName = TextFieldValue("")
                    diagnosedDate = TextFieldValue("")
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) {
            Text("저장")
        }
        diseases.forEach {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("- ${it.diseaseName} (${it.diagnosedDate})", modifier = Modifier.padding(bottom = 4.dp))
                Text("수정", color = MaterialTheme.colorScheme.primary, modifier = Modifier
                    .padding(start = 8.dp)
                    .clickable {
                        editingDisease = it
                        diseaseName = TextFieldValue(it.diseaseName)
                        diagnosedDate = TextFieldValue(it.diagnosedDate)
                    })
            }
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

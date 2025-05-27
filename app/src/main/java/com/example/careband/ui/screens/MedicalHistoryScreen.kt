package com.example.careband.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Text
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.careband.viewmodel.MedicalHistoryViewModel
import com.example.careband.data.model.*

@Composable
fun MedicalHistoryScreen() {
    val viewModel: MedicalHistoryViewModel = viewModel()
    val vaccinations by viewModel.vaccinationRecords.collectAsState()
    val medications by viewModel.medicationRecords.collectAsState()
    val diseases by viewModel.diseaseRecords.collectAsState()

    // 입력 상태 변수
    var vaccineName by remember { mutableStateOf(TextFieldValue("")) }
    var vaccineDate by remember { mutableStateOf(TextFieldValue("")) }

    var medicineName by remember { mutableStateOf(TextFieldValue("")) }
    var startDate by remember { mutableStateOf(TextFieldValue("")) }
    var endDate by remember { mutableStateOf(TextFieldValue("")) }

    var diseaseName by remember { mutableStateOf(TextFieldValue("")) }
    var diagnosedDate by remember { mutableStateOf(TextFieldValue("")) }

    LaunchedEffect(Unit) {
        viewModel.loadAllRecords()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // 🩺 접종 기록
        SectionTitle("접종 기록")

        BasicTextField(
            value = vaccineName,
            onValueChange = { vaccineName = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            decorationBox = { innerTextField -> innerTextField() }
        )
        BasicTextField(
            value = vaccineDate,
            onValueChange = { vaccineDate = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            decorationBox = { innerTextField -> innerTextField() }
        )
        Text(
            text = "➕ 추가",
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable {
                    if (vaccineName.text.isNotBlank() && vaccineDate.text.isNotBlank()) {
                        viewModel.addVaccinationRecord(
                            VaccinationRecord(
                                vaccineName = vaccineName.text,
                                date = vaccineDate.text,
                                userId = "test_user"
                            )
                        )
                        vaccineName = TextFieldValue("")
                        vaccineDate = TextFieldValue("")
                    }
                }
        )
        vaccinations.forEach {
            Text("- ${it.vaccineName} (${it.date})", modifier = Modifier.padding(bottom = 4.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 💊 복약 기록
        SectionTitle("복약 기록")

        BasicTextField(
            value = medicineName,
            onValueChange = { medicineName = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            decorationBox = { innerTextField -> innerTextField() }
        )
        BasicTextField(
            value = startDate,
            onValueChange = { startDate = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            decorationBox = { innerTextField -> innerTextField() }
        )
        BasicTextField(
            value = endDate,
            onValueChange = { endDate = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            decorationBox = { innerTextField -> innerTextField() }
        )
        Text(
            text = "➕ 추가",
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable {
                    if (medicineName.text.isNotBlank() && startDate.text.isNotBlank()) {
                        viewModel.addMedicationRecord(
                            MedicationRecord(
                                medicineName = medicineName.text,
                                startDate = startDate.text,
                                endDate = endDate.text,
                                userId = "test_user"
                            )
                        )
                        medicineName = TextFieldValue("")
                        startDate = TextFieldValue("")
                        endDate = TextFieldValue("")
                    }
                }
        )
        medications.forEach {
            Text("- ${it.medicineName} (${it.startDate} ~ ${it.endDate})", modifier = Modifier.padding(bottom = 4.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 🧾 질병 기록
        SectionTitle("질병 기록")

        BasicTextField(
            value = diseaseName,
            onValueChange = { diseaseName = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            decorationBox = { innerTextField -> innerTextField() }
        )
        BasicTextField(
            value = diagnosedDate,
            onValueChange = { diagnosedDate = it },
            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
            decorationBox = { innerTextField -> innerTextField() }
        )
        Text(
            text = "➕ 추가",
            modifier = Modifier
                .padding(vertical = 8.dp)
                .clickable {
                    if (diseaseName.text.isNotBlank() && diagnosedDate.text.isNotBlank()) {
                        viewModel.addDiseaseRecord(
                            DiseaseRecord(
                                diseaseName = diseaseName.text,
                                diagnosedDate = diagnosedDate.text,
                                userId = "test_user"
                            )
                        )
                        diseaseName = TextFieldValue("")
                        diagnosedDate = TextFieldValue("")
                    }
                }
        )
        diseases.forEach {
            Text("- ${it.diseaseName} (${it.diagnosedDate})", modifier = Modifier.padding(bottom = 4.dp))
        }
    }
}

@Composable
fun SectionTitle(title: String) {
    Text(
        text = title,
        style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold),
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

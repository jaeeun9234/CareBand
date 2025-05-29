package com.example.careband.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.careband.viewmodel.AuthViewModel
import com.example.careband.viewmodel.HealthViewModel
import com.example.careband.data.model.Note
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HealthRecordScreen(
    navController: NavHostController,
    viewModel: HealthViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val userId by authViewModel.userId.collectAsState()
    val today = rememberSaveable { mutableStateOf(LocalDate.now()) }
    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val dateStr = today.value.format(formatter)

    val record = viewModel.healthRecord.collectAsState().value
    val saveState by viewModel.saveState.collectAsState()
    var showNoteDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val weightText by viewModel.weightText.collectAsState()
    val systolicText by viewModel.systolicText.collectAsState()
    val diastolicText by viewModel.diastolicText.collectAsState()
    val glucoseFastingText by viewModel.glucoseFastingText.collectAsState()
    val glucosePostText by viewModel.glucosePostText.collectAsState()

    LaunchedEffect(userId) {
        if (userId != null) {
            viewModel.loadHealthRecord(userId!!, dateStr)
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        Button(onClick = {
            DatePickerDialog(
                context,
                { _, year, month, day ->
                    val newDate = LocalDate.of(year, month + 1, day)
                    today.value = newDate
                    if (userId != null) {
                        viewModel.loadHealthRecord(userId!!, newDate.format(formatter))
                    }
                },
                today.value.year,
                today.value.monthValue - 1,
                today.value.dayOfMonth
            ).show()
        }) {
            Text("날짜: $dateStr")
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value =  weightText,
            onValueChange = { viewModel.updateWeightText(it) },
            label = { Text("체중 (Kg)") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            placeholder = { Text("예: 60") }
        )

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value =  systolicText,
                onValueChange = { viewModel.updateSystolicText(it) },
                label = { Text("수축기") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("예: 120") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value =  diastolicText,
                onValueChange =  { viewModel.updateDiastolicText(it) },
                label = { Text("이완기") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("예: 80") }
            )
        }

        Row(modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value =  glucoseFastingText,
                onValueChange = { viewModel.updateGlucoseFastingText(it) },
                label = { Text("공복 혈당") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("예: 80") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value =  glucosePostText,
                onValueChange = { viewModel.updateGlucosePostText(it) },
                label = { Text("식후 혈당") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                placeholder = { Text("예: 120") }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("Note: ", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.weight(1f))
            IconButton(onClick = { showNoteDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }

        record.notes.forEach { note ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                Column(modifier = Modifier.padding(8.dp)) {
                    Text(note.title, style = MaterialTheme.typography.titleMedium)
                    Text(note.description, style = MaterialTheme.typography.bodySmall)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(onClick = {
            viewModel.saveManually()
        }) {
            Text("저장")
        }

        if (saveState != null) {
            Text(
                text = saveState ?: "",
                color = if (saveState!!.startsWith("저장 완료")) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }

    if (showNoteDialog) {
        NoteDialogScreen(
            onDismiss = { showNoteDialog = false },
            onSave = { note ->
                viewModel.addNote(note)
                showNoteDialog = false
            }
        )
    }
}

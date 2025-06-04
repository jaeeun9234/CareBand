package com.example.careband.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.careband.viewmodel.MedicalReportViewModel

enum class ReportPeriod {
    WEEK, MONTH
}

@Composable
fun MedicalReportScreen(
    navController: NavController,
    userId: String
) {
    val viewModel: MedicalReportViewModel = viewModel(factory = object : androidx.lifecycle.ViewModelProvider.Factory {
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return MedicalReportViewModel(userId) as T
        }
    })

    val summary by viewModel.reportSummary.collectAsState()
    var selectedPeriod by remember { mutableStateOf(ReportPeriod.WEEK) }

    LaunchedEffect(Unit) {
        viewModel.loadSummary()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = { selectedPeriod = ReportPeriod.WEEK },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedPeriod == ReportPeriod.WEEK) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("1주일")
            }
            Button(
                onClick = { selectedPeriod = ReportPeriod.MONTH },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (selectedPeriod == ReportPeriod.MONTH) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Text("1개월")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        fun selectInt(v7: Int, v30: Int): Int = if (selectedPeriod == ReportPeriod.WEEK) v7 else v30
        fun selectFloat(v7: Float, v30: Float): Float = if (selectedPeriod == ReportPeriod.WEEK) v7 else v30
        fun selectString(v7: String, v30: String): String = if (selectedPeriod == ReportPeriod.WEEK) v7 else v30

        MedicalReportCard("심박수 평균: ${selectInt(summary.avgBpm7d, summary.avgBpm30d)} BPM") {
            Text("• 이탈: ${selectInt(summary.bpmOutOfRangeCount7d, summary.bpmOutOfRangeCount30d)}회")
        }

        MedicalReportCard("혈압 평균: ${selectString(summary.avgBp7d, summary.avgBp30d)}") {
            Text("• 이탈: ${selectInt(summary.bpOutOfRangeCount7d, summary.bpOutOfRangeCount30d)}회")
        }

        MedicalReportCard("공복 혈당 평균: ${selectInt(summary.avgGlucoseFasting7d, summary.avgGlucoseFasting30d)} mg/dL") {
            Text("• 이탈: ${selectInt(summary.glucoseFastingOutOfRangeCount7d, summary.glucoseFastingOutOfRangeCount30d)}회")
        }

        MedicalReportCard("식후 혈당 평균: ${selectInt(summary.avgGlucosePost7d, summary.avgGlucosePost30d)} mg/dL") {
            Text("• 이탈: ${selectInt(summary.glucosePostOutOfRangeCount7d, summary.glucosePostOutOfRangeCount30d)}회")
        }

        MedicalReportCard("체중 평균: ${"%.1f".format(selectFloat(summary.avgWeight7d, summary.avgWeight30d))} kg") {
            Text("• 이탈: ${selectInt(summary.weightOutOfRangeCount7d, summary.weightOutOfRangeCount30d)}회")
        }

        MedicalReportCard("산소포화도 평균: ${selectInt(summary.avgSpO27d, summary.avgSpO230d)}%") {
            Text("• 이탈: ${selectInt(summary.spo2OutOfRangeCount7d, summary.spo2OutOfRangeCount30d)}회")
        }

        MedicalReportCard("체온 평균: ${"%.1f".format(selectFloat(summary.avgTemp7d, summary.avgTemp30d))}℃") {
            Text("• 이탈: ${selectInt(summary.tempOutOfRangeCount7d, summary.tempOutOfRangeCount30d)}회")
        }

        MedicalReportCard("낙상 감지 횟수") {
            Text("• 감지 횟수: ${selectInt(summary.fallCount7d, summary.fallCount30d)}회")
        }
    }
}

@Composable
fun MedicalReportCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            content()
        }
    }
}

package com.example.careband.ui.screens

import androidx.compose.foundation.clickable
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
    val abnormalBpmDates by viewModel.abnormalBpmDates.collectAsState()
    val abnormalBpDates7d by viewModel.abnormalBpDates7d.collectAsState()
    val abnormalBpDates30d by viewModel.abnormalBpDates30d.collectAsState()
    val abnormalSpO2Dates by viewModel.abnormalSpO2Dates.collectAsState()
    val abnormalTempDates by viewModel.abnormalTempDates.collectAsState()
    val abnormalGlucoseFastingDates by viewModel.abnormalGlucoseFastingDates.collectAsState()
    val abnormalGlucosePostDates by viewModel.abnormalGlucosePostDates.collectAsState()
    val abnormalWeightDates by viewModel.abnormalWeightDates.collectAsState()
    val fallDetectedDates by viewModel.fallDetectedDates.collectAsState()

    var selectedPeriod by remember { mutableStateOf(ReportPeriod.WEEK) }

    var expandBpm by remember { mutableStateOf(false) }
    var expandBp by remember { mutableStateOf(false) }
    var expandSpO2 by remember { mutableStateOf(false) }
    var expandTemp by remember { mutableStateOf(false) }
    var expandGlucoseFasting by remember { mutableStateOf(false) }
    var expandGlucosePost by remember { mutableStateOf(false) }
    var expandWeight by remember { mutableStateOf(false) }
    var expandFall by remember { mutableStateOf(false) }

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
        fun selectBpDates(): List<String> = if (selectedPeriod == ReportPeriod.WEEK) abnormalBpDates7d else abnormalBpDates30d

        ExpandableReportCard(
            title = "심박수 평균: ${selectInt(summary.avgBpm7d, summary.avgBpm30d)} BPM",
            subtitle = "비정상 감지: ${selectInt(summary.bpmOutOfRangeCount7d, summary.bpmOutOfRangeCount30d)}회",
            expanded = expandBpm,
            onToggle = { expandBpm = !expandBpm },
            abnormalDates = abnormalBpmDates
        )

        ExpandableReportCard(
            title = "혈압 평균: ${selectString(summary.avgBp7d, summary.avgBp30d)}",
            subtitle = "비정상 감지: ${selectInt(summary.bpOutOfRangeCount7d, summary.bpOutOfRangeCount30d)}회",
            expanded = expandBp,
            onToggle = { expandBp = !expandBp },
            abnormalDates = selectBpDates()
        )

        ExpandableReportCard(
            title = "공복 혈당 평균: ${selectInt(summary.avgGlucoseFasting7d, summary.avgGlucoseFasting30d)} mg/dL",
            subtitle = "비정상 감지: ${selectInt(summary.glucoseFastingOutOfRangeCount7d, summary.glucoseFastingOutOfRangeCount30d)}회",
            expanded = expandGlucoseFasting,
            onToggle = { expandGlucoseFasting = !expandGlucoseFasting },
            abnormalDates = abnormalGlucoseFastingDates
        )

        ExpandableReportCard(
            title = "식후 혈당 평균: ${selectInt(summary.avgGlucosePost7d, summary.avgGlucosePost30d)} mg/dL",
            subtitle = "비정상 감지: ${selectInt(summary.glucosePostOutOfRangeCount7d, summary.glucosePostOutOfRangeCount30d)}회",
            expanded = expandGlucosePost,
            onToggle = { expandGlucosePost = !expandGlucosePost },
            abnormalDates = abnormalGlucosePostDates
        )

        ExpandableReportCard(
            title = "체중 평균: ${"%.1f".format(selectFloat(summary.avgWeight7d, summary.avgWeight30d))} kg",
            subtitle = "비정상 감지: ${selectInt(summary.weightOutOfRangeCount7d, summary.weightOutOfRangeCount30d)}회",
            expanded = expandWeight,
            onToggle = { expandWeight = !expandWeight },
            abnormalDates = abnormalWeightDates
        )

        ExpandableReportCard(
            title = "산소포화도 평균: ${selectInt(summary.avgSpO27d, summary.avgSpO230d)}%",
            subtitle = "비정상 감지: ${selectInt(summary.spo2OutOfRangeCount7d, summary.spo2OutOfRangeCount30d)}회",
            expanded = expandSpO2,
            onToggle = { expandSpO2 = !expandSpO2 },
            abnormalDates = abnormalSpO2Dates
        )

        ExpandableReportCard(
            title = "체온 평균: ${"%.1f".format(selectFloat(summary.avgTemp7d, summary.avgTemp30d))}℃",
            subtitle = "비정상 감지: ${selectInt(summary.tempOutOfRangeCount7d, summary.tempOutOfRangeCount30d)}회",
            expanded = expandTemp,
            onToggle = { expandTemp = !expandTemp },
            abnormalDates = abnormalTempDates
        )

        ExpandableReportCard(
            title = "낙상 감지 횟수: ${selectInt(summary.fallCount7d, summary.fallCount30d)}회",
            subtitle = "감지된 날짜 보기",
            expanded = expandFall,
            onToggle = { expandFall = !expandFall },
            abnormalDates = fallDetectedDates
        )
    }
}

@Composable
fun ExpandableReportCard(
    title: String,
    subtitle: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    abnormalDates: List<String>
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clickable { onToggle() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodySmall)
            if (expanded) {
                Spacer(modifier = Modifier.height(4.dp))
                abnormalDates.forEach { date ->
                    Text("• $date", color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                }
            }
        }
    }
}

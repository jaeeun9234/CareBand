package com.example.careband.ui.screens

// 필요한 라이브러리 import
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.core.axis.formatter.AxisValueFormatter
import java.time.LocalDate
import com.example.careband.viewmodel.VitalSignsViewModel
import com.example.careband.viewmodel.VitalSignsViewModelFactory
import com.example.careband.data.model.VitalSignsRecord
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.compose.axis.vertical.rememberStartAxis
import com.patrykandpatrick.vico.compose.axis.horizontal.rememberBottomAxis
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.rememberLazyListState
import com.patrykandpatrick.vico.core.axis.Axis
import com.patrykandpatrick.vico.core.axis.AxisPosition
import com.patrykandpatrick.vico.core.entry.entryModelOf
import com.patrykandpatrick.vico.core.formatter.ValueFormatter

@Composable
fun VitalSignsChartScreen(
    userId: String,
    viewModel: VitalSignsViewModel = viewModel(factory = VitalSignsViewModelFactory(userId))
) {
    val records by viewModel.records.collectAsState()
    val healthRecords by viewModel.healthRecords.collectAsState()
    val selectedRange = remember { mutableStateOf(7) }
    val today = LocalDate.now()
    val listState = rememberLazyListState()

    LaunchedEffect(selectedRange.value) {
        val fromDate = today.minusDays(selectedRange.value.toLong())
        viewModel.loadVitalRecords(fromDate)
        viewModel.loadHealthRecords(fromDate)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        state = listState,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // 날짜 범위 선택
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(onClick = { selectedRange.value = 7 }) { Text("7일간") }
                Button(onClick = { selectedRange.value = 30 }) { Text("1개월") }
            }
        }

        // VitalSignsRecord 그래프들
        item {
            Text("심박수 (BPM)", style = MaterialTheme.typography.titleMedium)
            VitalLineChart(records.map { it.heartRate }, records.map { it.timestamp })
        }

        item {
            Text("산소포화도 (%)", style = MaterialTheme.typography.titleMedium)
            VitalLineChart(records.map { it.spo2 }, records.map { it.timestamp })
        }

        item {
            Text("체온 (°C)", style = MaterialTheme.typography.titleMedium)
            VitalLineChart(records.map { it.bodyTemp.toInt() }, records.map { it.timestamp })
        }

        // HealthRecord 그래프들
        item {
            Text("체중 (kg)", style = MaterialTheme.typography.titleMedium)
            VitalLineChart(healthRecords.map { it.weight }, healthRecords.map { it.date })
        }

        item {
            Text("수축기 혈압 (mmHg)", style = MaterialTheme.typography.titleMedium)
            VitalLineChart(healthRecords.map { it.systolic }, healthRecords.map { it.date })
        }

        item {
            Text("이완기 혈압 (mmHg)", style = MaterialTheme.typography.titleMedium)
            VitalLineChart(healthRecords.map { it.diastolic }, healthRecords.map { it.date })
        }

        item {
            Text("식후 혈당 (mg/dL)", style = MaterialTheme.typography.titleMedium)
            VitalLineChart(healthRecords.map { it.glucosePost }, healthRecords.map { it.date })
        }

        item {
            Text("공복 혈당 (mg/dL)", style = MaterialTheme.typography.titleMedium)
            VitalLineChart(healthRecords.map { it.glucoseFasting }, healthRecords.map { it.date })
        }
    }
}

@Composable
fun VitalLineChart(values: List<Int>, labels: List<String>) {
    if (values.isEmpty()) {
        Text("데이터 없음", style = MaterialTheme.typography.bodyMedium)
        return
    }

    val entries = entryModelOf(*values.mapIndexed { i, v -> i to v }.toTypedArray())

    val formattedLabels = labels.map {
        if (it.length >= 10) it.substring(5).replace("-", "/") else it
    }

    val labelMap = formattedLabels.mapIndexed { i, label -> i.toFloat() to label }.toMap()

    val customFormatter = AxisValueFormatter<AxisPosition.Horizontal.Bottom> { value, _ ->
        labelMap[value] ?: ""
    }

    Chart(
        chart = lineChart(),
        model = entries,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(valueFormatter = customFormatter),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    )
}

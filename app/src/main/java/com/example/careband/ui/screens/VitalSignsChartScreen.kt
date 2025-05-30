// 필요한 라이브러리 import
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.compose.chart.line.LineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun VitalSignsChartScreen(
    userId: String,
    viewModel: VitalSignsViewModel = viewModel(factory = VitalSignsViewModelFactory(userId))
) {
    val records by viewModel.filteredRecords.collectAsState()
    val selectedRange = remember { mutableStateOf(7) } // 기본값: 7일
    val today = LocalDate.now()

    Column(modifier = Modifier.padding(16.dp)) {
        // 날짜 범위 선택
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            Button(onClick = { selectedRange.value = 7 }) { Text("7일간") }
            Button(onClick = { selectedRange.value = 30 }) { Text("1개월") }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 그래프들 표시
        Text("심박수 (BPM)", style = MaterialTheme.typography.titleMedium)
        VitalLineChart(records.map { it.heartRate }, records.map { it.timestamp })

        Spacer(modifier = Modifier.height(8.dp))

        Text("산소포화도 (%)", style = MaterialTheme.typography.titleMedium)
        VitalLineChart(records.map { it.spo2 }, records.map { it.timestamp })

        Spacer(modifier = Modifier.height(8.dp))

        Text("체온 (\u00b0C)", style = MaterialTheme.typography.titleMedium)
        VitalLineChart(records.map { it.bodyTemp.toInt() }, records.map { it.timestamp })
    }

    // 데이터 로딩
    LaunchedEffect(selectedRange.value) {
        val fromDate = today.minusDays(selectedRange.value.toLong())
        viewModel.loadVitalRecords(fromDate)
    }
}

@Composable
fun VitalLineChart(values: List<Int>, labels: List<String>) {
    if (values.isEmpty()) {
        Text("데이터 없음", style = MaterialTheme.typography.bodyMedium)
        return
    }

    val entries = entryModelOf(*values.mapIndexed { i, v -> i to v }.toTypedArray())

    Chart(
        chart = lineChart(),
        model = entries,
        startAxis = rememberStartAxis(),
        bottomAxis = rememberBottomAxis(),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    )
}

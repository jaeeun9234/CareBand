package com.example.careband.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careband.data.model.ReportSummary
import com.example.careband.data.repository.HealthRepository
import com.example.careband.data.repository.VitalSignsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MedicalReportViewModel(private val userId: String) : ViewModel() {

    private val healthRepository = HealthRepository()
    private val vitalRepository = VitalSignsRepository()

    private val _reportSummary = MutableStateFlow(ReportSummary())
    val reportSummary: StateFlow<ReportSummary> = _reportSummary

    fun loadSummary() {
        viewModelScope.launch {
            val now = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val today = now.format(formatter)
            val sevenDaysAgo = now.minusDays(7).format(formatter)
            val thirtyDaysAgo = now.minusDays(30).format(formatter)

            val records7d = healthRepository.getHealthRecordsInRange(userId, sevenDaysAgo, today)
            val records30d = healthRepository.getHealthRecordsInRange(userId, thirtyDaysAgo, today)

            val vitals7d = vitalRepository.getVitalSignsInRange(userId, sevenDaysAgo, today)
            val vitals30d = vitalRepository.getVitalSignsInRange(userId, thirtyDaysAgo, today)

            fun <T : Number> List<T>.averageInt(): Int =
                if (isEmpty()) 0 else map { it.toDouble() }.average().toInt()

            fun <T : Number> List<T>.averageFloat(): Float =
                if (isEmpty()) 0f else map { it.toDouble() }.average().toFloat()

            // 평균값 계산 (7일 & 30일)
            val avgBpm7d = vitals7d.map { it.heartRate }.averageInt()
            val avgBpm30d = vitals30d.map { it.heartRate }.averageInt()

            val avgWeight7d = records7d.map { it.weight }.averageFloat()
            val avgWeight30d = records30d.map { it.weight }.averageFloat()

            val avgGlucoseFasting7d = records7d.map { it.glucoseFasting }.averageInt()
            val avgGlucoseFasting30d = records30d.map { it.glucoseFasting }.averageInt()

            val avgGlucosePost7d = records7d.map { it.glucosePost }.averageInt()
            val avgGlucosePost30d = records30d.map { it.glucosePost }.averageInt()

            val avgSpO27d = vitals7d.map { it.spo2 }.averageInt()
            val avgSpO230d = vitals30d.map { it.spo2 }.averageInt()

            val avgTemp7d = vitals7d.map { it.bodyTemp }.averageFloat()
            val avgTemp30d = vitals30d.map { it.bodyTemp }.averageFloat()

            val avgSystolic7d = records7d.map { it.systolic }.averageInt()
            val avgDiastolic7d = records7d.map { it.diastolic }.averageInt()
            val avgBp7dStr = "$avgSystolic7d/$avgDiastolic7d"

            val avgSystolic30d = records30d.map { it.systolic }.averageInt()
            val avgDiastolic30d = records30d.map { it.diastolic }.averageInt()
            val avgBp30dStr = "$avgSystolic30d/$avgDiastolic30d"

            fun countOutOfRange(range: IntRange, week: List<Int>, month: List<Int>) =
                week.count { it !in range } to month.count { it !in range }

            fun countOutOfRangeFloat(range: ClosedFloatingPointRange<Float>, week: List<Float>, month: List<Float>) =
                week.count { it !in range } to month.count { it !in range }

            val (bpmOut7d, bpmOut30d) = countOutOfRange(60..100, vitals7d.map { it.heartRate }, vitals30d.map { it.heartRate })
            val bpOut7d = records7d.count { it.systolic !in 90..120 || it.diastolic !in 60..80 }
            val bpOut30d = records30d.count { it.systolic !in 90..120 || it.diastolic !in 60..80 }

            val (glucoseFastingOut7d, glucoseFastingOut30d) = countOutOfRange(70..100, records7d.map { it.glucoseFasting }, records30d.map { it.glucoseFasting })
            val (glucosePostOut7d, glucosePostOut30d) = countOutOfRange(70..140, records7d.map { it.glucosePost }, records30d.map { it.glucosePost })
            val (weightOut7d, weightOut30d) = countOutOfRange(45..80, records7d.map { it.weight }, records30d.map { it.weight })
            val (spo2Out7d, spo2Out30d) = countOutOfRange(95..100, vitals7d.map { it.spo2 }, vitals30d.map { it.spo2 })
            val (tempOut7d, tempOut30d) = countOutOfRangeFloat(36.1f..37.2f, vitals7d.map { it.bodyTemp }, vitals30d.map { it.bodyTemp })

            val fallCount7d = vitals7d.count { it.fallDetected }
            val fallCount30d = vitals30d.count { it.fallDetected }

            _reportSummary.value = ReportSummary(
                avgBpm7d = avgBpm7d,
                avgBpm30d = avgBpm30d,
                avgBp7d = avgBp7dStr,
                avgBp30d = avgBp30dStr,
                avgGlucoseFasting7d = avgGlucoseFasting7d,
                avgGlucoseFasting30d = avgGlucoseFasting30d,
                avgGlucosePost7d = avgGlucosePost7d,
                avgGlucosePost30d = avgGlucosePost30d,
                avgWeight7d = avgWeight7d,
                avgWeight30d = avgWeight30d,
                avgSpO27d = avgSpO27d,
                avgSpO230d = avgSpO230d,
                avgTemp7d = avgTemp7d,
                avgTemp30d = avgTemp30d,

                bpmOutOfRangeCount7d = bpmOut7d,
                bpmOutOfRangeCount30d = bpmOut30d,
                bpOutOfRangeCount7d = bpOut7d,
                bpOutOfRangeCount30d = bpOut30d,
                glucoseFastingOutOfRangeCount7d = glucoseFastingOut7d,
                glucoseFastingOutOfRangeCount30d = glucoseFastingOut30d,
                glucosePostOutOfRangeCount7d = glucosePostOut7d,
                glucosePostOutOfRangeCount30d = glucosePostOut30d,
                weightOutOfRangeCount7d = weightOut7d,
                weightOutOfRangeCount30d = weightOut30d,
                spo2OutOfRangeCount7d = spo2Out7d,
                spo2OutOfRangeCount30d = spo2Out30d,
                tempOutOfRangeCount7d = tempOut7d,
                tempOutOfRangeCount30d = tempOut30d,
                fallCount7d = fallCount7d,
                fallCount30d = fallCount30d
            )
        }
    }
}
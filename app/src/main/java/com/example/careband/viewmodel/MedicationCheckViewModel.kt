package com.example.careband.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careband.data.model.MedicationRecord
import com.example.careband.data.repository.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MedicationCheckViewModel(
    private val repository: MedicationRepository = MedicationRepository()
) : ViewModel() {

    private val sdf = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    private val today: String = sdf.format(Date())

    private val _todayMedications = MutableStateFlow<List<MedicationRecord>>(emptyList())
    val todayMedications: StateFlow<List<MedicationRecord>> = _todayMedications

    // 복약 정보 필터링하여 StateFlow에 저장
    fun loadTodayMedications(userId: String) {
        viewModelScope.launch {
            val allRecords = repository.getMedicationRecords(userId)

            val filtered = allRecords.filter { record ->
                val isInPeriod = record.startDate <= today &&
                        (record.endDate.isBlank() || today <= record.endDate)
                isInPeriod // ✅ 오늘 복약 대상이면 모두 표시
            }

            _todayMedications.value = filtered
        }
    }


    // 체크 시 takenDates에 오늘 날짜 추가 후 업데이트
    fun updateMedicationCheckState(userId: String, record: MedicationRecord, isChecked: Boolean) {
        val today = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(Date())

        val updatedTakenDates = if (isChecked) {
            record.takenDates + today // 복약 완료
        } else {
            record.takenDates - today // 복약 취소
        }

        val updated = record.copy(takenDates = updatedTakenDates)

        viewModelScope.launch {
            repository.updateMedicationRecord(userId, updated)
            loadTodayMedications(userId) // UI 업데이트
        }
    }

}

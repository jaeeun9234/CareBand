package com.example.careband.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careband.data.model.HealthRecord
import com.example.careband.data.model.Note
import com.example.careband.data.repository.HealthRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HealthViewModel : ViewModel() {

    private val repository = HealthRepository()
    private val _healthRecord = MutableStateFlow(HealthRecord())
    val healthRecord: StateFlow<HealthRecord> = _healthRecord

    private val _weightText = MutableStateFlow("")
    val weightText: StateFlow<String> = _weightText
    private val _systolicText = MutableStateFlow("")
    val systolicText: StateFlow<String> = _systolicText

    private val _diastolicText = MutableStateFlow("")
    val diastolicText: StateFlow<String> = _diastolicText

    private val _glucoseFastingText = MutableStateFlow("")
    val glucoseFastingText: StateFlow<String> = _glucoseFastingText

    private val _glucosePostText = MutableStateFlow("")
    val glucosePostText: StateFlow<String> = _glucosePostText

    private val _saveState = MutableStateFlow<String?>(null)
    val saveState: StateFlow<String?> = _saveState

    private var currentUserId: String = ""
    private var currentDate: String = ""

//    fun loadHealthRecord(userId: String, date: String) {
//        currentUserId = userId
//        currentDate = date
//        viewModelScope.launch {
//            repository.getHealthRecord(userId, date) { record ->
//                _healthRecord.value = record ?: HealthRecord(date = date)
//            }
//        }
//    }

    fun updateWeight(value: Int) {
        _healthRecord.value = _healthRecord.value.copy(weight = value)
    }

    fun updateSystolic(value: Int) {
        _healthRecord.value = _healthRecord.value.copy(systolic = value)
    }

    fun updateDiastolic(value: Int) {
        _healthRecord.value = _healthRecord.value.copy(diastolic = value)
    }

    fun updateGlucoseFasting(value: Int) {
        _healthRecord.value = _healthRecord.value.copy(glucoseFasting = value)
    }

    fun updateGlucosePost(value: Int) {
        _healthRecord.value = _healthRecord.value.copy(glucosePost = value)
    }

    fun addNote(note: Note) {
        val updatedNotes = _healthRecord.value.notes + note
        _healthRecord.value = _healthRecord.value.copy(notes = updatedNotes)
    }

    fun saveManually() {
        updateRecord(_healthRecord.value)
    }

    fun updateWeightText(text: String) {
        _weightText.value = text
        val weight = text.toIntOrNull() ?: 0
        _healthRecord.value = _healthRecord.value.copy(weight = weight)
    }

    fun updateSystolicText(text: String) {
        _systolicText.value = text
        _healthRecord.value = _healthRecord.value.copy(systolic = text.toIntOrNull() ?: 0)
    }

    fun updateDiastolicText(text: String) {
        _diastolicText.value = text
        _healthRecord.value = _healthRecord.value.copy(diastolic = text.toIntOrNull() ?: 0)
    }

    fun updateGlucoseFastingText(text: String) {
        _glucoseFastingText.value = text
        _healthRecord.value = _healthRecord.value.copy(glucoseFasting = text.toIntOrNull() ?: 0)
    }

    fun updateGlucosePostText(text: String) {
        _glucosePostText.value = text
        _healthRecord.value = _healthRecord.value.copy(glucosePost = text.toIntOrNull() ?: 0)
    }

    fun loadHealthRecord(userId: String, date: String) {
        currentUserId = userId
        currentDate = date
        viewModelScope.launch {
            repository.getHealthRecord(userId, date) { record ->
                val newRecord = record ?: HealthRecord(date = date)
                _healthRecord.value = record ?: HealthRecord(
                    id = "healthRecord:$userId:$date",
                    userId = userId,
                    date = date
                )

                // ✅ 각 필드별 텍스트 상태 설정
                _weightText.value = if (newRecord.weight == 0) "" else newRecord.weight.toString()
                _systolicText.value = if (newRecord.systolic == 0) "" else newRecord.systolic.toString()
                _diastolicText.value = if (newRecord.diastolic == 0) "" else newRecord.diastolic.toString()
                _glucoseFastingText.value = if (newRecord.glucoseFasting == 0) "" else newRecord.glucoseFasting.toString()
                _glucosePostText.value = if (newRecord.glucosePost == 0) "" else newRecord.glucosePost.toString()
            }
        }
    }

    private fun updateRecord(newRecord: HealthRecord) {
        viewModelScope.launch {
            repository.saveHealthRecord(currentUserId, newRecord,
                onSuccess = {
                    _saveState.value = "저장 완료"
                    viewModelScope.launch {
                        delay(2000)
                        _saveState.value = null
                    }
                },
                onFailure = {
                    _saveState.value = "저장 실패: $it"
                    viewModelScope.launch {
                        delay(3000)
                        _saveState.value = null
                    }
                }
            )
        }
    }
}

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

    private val _saveState = MutableStateFlow<String?>(null)
    val saveState: StateFlow<String?> = _saveState

    private var currentUserId: String = ""
    private var currentDate: String = ""

    fun loadHealthRecord(userId: String, date: String) {
        currentUserId = userId
        currentDate = date
        viewModelScope.launch {
            repository.getHealthRecord(userId, date) { record ->
                _healthRecord.value = record ?: HealthRecord(date = date)
            }
        }
    }

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

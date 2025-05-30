package com.example.careband.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careband.data.model.MedicationRecord
import com.example.careband.data.repository.MedicationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MedicationViewModel(private val userId: String) : ViewModel() {

    private val repository = MedicationRepository()

    private val _medicationRecords = MutableStateFlow<List<MedicationRecord>>(emptyList())
    val medicationRecords: StateFlow<List<MedicationRecord>> = _medicationRecords

    init {
        loadMedicationRecords()
    }

    fun loadMedicationRecords() {
        viewModelScope.launch {
            _medicationRecords.value = repository.getMedicationRecords(userId)
        }
    }

    fun addMedicationRecord(record: MedicationRecord) {
        viewModelScope.launch {
            repository.addMedicationRecord(userId, record)
            loadMedicationRecords()
        }
    }

    fun updateMedicationRecord(record: MedicationRecord) {
        viewModelScope.launch {
            repository.updateMedicationRecord(userId, record)
            loadMedicationRecords()
        }
    }

    fun deleteMedicationRecord(recordId: String) {
        viewModelScope.launch {
            repository.deleteMedicationRecord(userId, recordId)
            loadMedicationRecords()
        }
    }
}

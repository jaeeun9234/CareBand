package com.example.careband.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careband.data.model.VaccinationRecord
import com.example.careband.data.repository.VaccinationRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VaccinationViewModel(private val userId: String) : ViewModel() {

    private val repository = VaccinationRepository()

    private val _vaccinationRecords = MutableStateFlow<List<VaccinationRecord>>(emptyList())
    val vaccinationRecords: StateFlow<List<VaccinationRecord>> = _vaccinationRecords

    init {
        loadVaccinationRecords()
    }

    fun loadVaccinationRecords() {
        viewModelScope.launch {
            _vaccinationRecords.value = repository.getVaccinationRecords(userId)
        }
    }

    fun addVaccinationRecord(record: VaccinationRecord) {
        viewModelScope.launch {
            repository.addVaccinationRecord(userId, record)
            loadVaccinationRecords()
        }
    }

    fun updateVaccinationRecord(record: VaccinationRecord) {
        viewModelScope.launch {
            repository.updateVaccinationRecord(userId, record)
            loadVaccinationRecords()
        }
    }

    fun deleteVaccinationRecord(recordId: String) {
        viewModelScope.launch {
            repository.deleteVaccinationRecord(userId, recordId)
            loadVaccinationRecords()
        }
    }
}

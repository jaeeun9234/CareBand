package com.example.careband.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careband.data.model.*
import com.example.careband.data.repository.MedicalHistoryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MedicalHistoryViewModel(private val userId: String) : ViewModel() {

    private val repository = MedicalHistoryRepository()

    // ---------------- State ----------------
    private val _vaccinationRecords = MutableStateFlow<List<VaccinationRecord>>(emptyList())
    val vaccinationRecords: StateFlow<List<VaccinationRecord>> = _vaccinationRecords

    private val _medicationRecords = MutableStateFlow<List<MedicationRecord>>(emptyList())
    val medicationRecords: StateFlow<List<MedicationRecord>> = _medicationRecords

    private val _diseaseRecords = MutableStateFlow<List<DiseaseRecord>>(emptyList())
    val diseaseRecords: StateFlow<List<DiseaseRecord>> = _diseaseRecords

    // ---------------- Load ----------------
    fun loadAllRecords() {
        viewModelScope.launch {
            _vaccinationRecords.value = repository.getVaccinationRecords(userId)
            _medicationRecords.value = repository.getMedicationRecords(userId)
            _diseaseRecords.value = repository.getDiseaseRecords(userId)
        }
    }

    // ---------------- Add ----------------
    fun addVaccinationRecord(record: VaccinationRecord) {
        viewModelScope.launch {
            repository.addVaccinationRecord(userId, record)
            loadAllRecords() // 갱신
        }
    }

    fun addMedicationRecord(record: MedicationRecord) {
        viewModelScope.launch {
            repository.addMedicationRecord(userId, record)
            loadAllRecords()
        }
    }

    fun addDiseaseRecord(record: DiseaseRecord) {
        viewModelScope.launch {
            repository.addDiseaseRecord(userId, record)
            loadAllRecords()
        }
    }

    // ---------------- Update ----------------
    fun updateVaccinationRecord(record: VaccinationRecord) {
        viewModelScope.launch {
            repository.updateVaccinationRecord(userId, record)
            loadAllRecords()
        }
    }

    fun updateMedicationRecord(record: MedicationRecord) {
        viewModelScope.launch {
            repository.updateMedicationRecord(userId, record)
            loadAllRecords()
        }
    }

    fun updateDiseaseRecord(record: DiseaseRecord) {
        viewModelScope.launch {
            repository.updateDiseaseRecord(userId, record)
            loadAllRecords()
        }
    }
}

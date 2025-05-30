package com.example.careband.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careband.data.model.HealthRecord
import com.example.careband.data.model.VitalSignsRecord
import com.example.careband.data.repository.HealthRepository
import com.example.careband.data.repository.VitalSignsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import kotlinx.coroutines.launch

class VitalSignsViewModel(private val userId: String) : ViewModel() {

    private val repository = VitalSignsRepository()
    private val healthRepository = HealthRepository()

    private val _records = MutableStateFlow<List<VitalSignsRecord>>(emptyList())
    val records: StateFlow<List<VitalSignsRecord>> = _records

    private val _healthRecords = MutableStateFlow<List<HealthRecord>>(emptyList())
    val healthRecords: StateFlow<List<HealthRecord>> = _healthRecords


    fun loadVitalSignsInRange(startDate: String, endDate: String) {
        viewModelScope.launch {
            val data = repository.getVitalSignsInRange(userId, startDate, endDate)
            _records.value = data
        }
    }
    fun loadVitalRecords(fromDate: LocalDate) {
        val startDate = fromDate.toString()
        val endDate = LocalDate.now().toString()
        loadVitalSignsInRange(startDate, endDate)
    }
    fun loadHealthRecords(fromDate: LocalDate) {
        val startDate = fromDate.toString()
        val endDate = LocalDate.now().toString()

        viewModelScope.launch {
            val data = healthRepository.getHealthRecordsInRange(userId, startDate, endDate)
            _healthRecords.value = data
        }
    }
}

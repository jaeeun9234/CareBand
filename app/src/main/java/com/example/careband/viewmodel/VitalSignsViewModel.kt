package com.example.careband.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careband.data.model.VitalSignsRecord
import com.example.careband.data.repository.VitalSignsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class VitalSignsViewModel(private val userId: String) : ViewModel() {

    private val repository = VitalSignsRepository()

    private val _records = MutableStateFlow<List<VitalSignsRecord>>(emptyList())
    val records: StateFlow<List<VitalSignsRecord>> = _records

    fun loadVitalSignsInRange(startDate: String, endDate: String) {
        viewModelScope.launch {
            val data = repository.getVitalSignsInRange(userId, startDate, endDate)
            _records.value = data
        }
    }
}

package com.example.careband.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careband.data.repository.CaregiverRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CaregiverViewModel : ViewModel() {

    private val repository = CaregiverRepository()

    private val _caredUserId = MutableStateFlow<String?>(null)
    val caredUserId: StateFlow<String?> = _caredUserId

    fun loadCaredUserId(caregiverId: String) {
        viewModelScope.launch {
            val result = repository.getCaredUserId(caregiverId)
            _caredUserId.value = result
        }
    }

    // 필요하면 여기에 보호자 이름 등도 추가로 관리 가능
}

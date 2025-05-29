package com.example.careband.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class MedicalHistoryViewModelFactory(
    private val userId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MedicalHistoryViewModel::class.java)) {
            return MedicalHistoryViewModel(userId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

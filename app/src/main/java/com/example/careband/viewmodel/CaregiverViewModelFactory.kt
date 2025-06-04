package com.example.careband.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

class CaregiverViewModelFactory(
    private val caregiverId: String
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CaregiverViewModel::class.java)) {
            val viewModel = CaregiverViewModel()
            viewModel.loadCaredUserId(caregiverId)
            return viewModel as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

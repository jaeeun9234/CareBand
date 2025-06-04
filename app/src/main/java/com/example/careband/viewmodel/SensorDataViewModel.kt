package com.example.careband.viewmodel

import SensorData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.Timestamp
import java.util.*


class SensorDataViewModel(private val userId: String) : ViewModel() {
    var fallDetected: Boolean = false
        private set

    fun updateFromBle(uuid: UUID, value: String) {
        if (uuid.toString().equals("0000abcd-0000-1000-8000-00805f9b34fb", ignoreCase = true)) {
            fallDetected = value == "FALL"

            val sensorData = SensorData(
                fallDetected = fallDetected,
                timestamp = Timestamp.now()
            )

            // userId 전달이 필요 → 생성자에 userId 보존
            SensorDataRepository.uploadSensorData(userId, sensorData)
        }
    }
}
class SensorDataViewModelFactory(private val userId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SensorDataViewModel(userId) as T
    }
}
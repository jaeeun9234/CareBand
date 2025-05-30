package com.example.careband.data.model

import com.google.firebase.Timestamp

data class Alert(
    val alertId: Int = 0,                   // 알림 ID
    val userId: String = "",                // 사용자 ID
    val alertType: String = "",             // 알림 종류: "fall", "spo2_low" 등
    val isFalseAlarm: Boolean = false,      // 오작동 여부
    val notifiedTo: String = "",            // 수신자 (예: 보호자 ID)
    val responseReceived: Boolean = false,  // 응답 여부
    val timestamp: Timestamp = Timestamp.now() // Firebase Timestamp 사용
)

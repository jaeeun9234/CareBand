package com.example.careband.data.model

data class VitalSignsRecord(
    val date: String = "",
    val id: String = "",              // record_id: 고유 ID (문자열 ID로 관리)
    val userId: String = "",          // user_id: 사용자 고유 ID (FK 역할)
    val heartRate: Int = 0,           // 심박수 (BPM)
    val spo2: Int = 0,                // 산소포화도 (%)
    val bodyTemp: Float = 0.0f,       // 체온 (°C)
    val fallDetected: Boolean = false,// 낙상 여부
    val timestamp: String = ""        // 측정 시각 (ISO 8601 또는 yyyy-MM-dd HH:mm:ss)
)

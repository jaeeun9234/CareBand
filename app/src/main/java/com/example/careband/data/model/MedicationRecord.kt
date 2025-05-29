package com.example.careband.data.model

data class MedicationRecord(
    val id: String = "",
    val medicineName: String = "",
    val dosage: String = "", // 복용량
    val frequency: String = "", // 복용 빈도 예: 하루 1회
    val startDate: String = "",
    val endDate: String = "",
    val memo: String = "",
    val userId: String = ""
)

package com.example.careband.data.model

// 기존 단일 Note → 복수 Notes로 확장

data class HealthRecord(
    val date: String = "",
    val weight: Int = 0,
    val systolic: Int = 0,
    val diastolic: Int = 0,
    val glucoseFasting: Int = 0,
    val glucosePost: Int = 0,
    val notes: List<Note> = emptyList()
)

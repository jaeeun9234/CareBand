package com.example.careband.data.model

data class DiseaseRecord(
    val id: String = "",
    val userId: String = "",
    val diseaseName: String = "",
    val diagnosedDate: String = "", // 시작일
    val endDate: String? = null,    // 종료일 (없을 수도 있으므로 nullable)
    val treatment: String = "",     // 치료 내용
    val doctor: String = "",        // 담당의
    val memo: String = ""           // 메모 (기존 필드 유지)
)

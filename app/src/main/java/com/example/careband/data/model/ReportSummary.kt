package com.example.careband.data.model

data class ReportSummary(
    // 평균값
    val avgBpm7d: Int = 0,
    val avgBpm30d: Int = 0,
    val avgWeight7d: Float = 0f,
    val avgWeight30d: Float = 0f,
    val avgGlucoseFasting7d: Int = 0,
    val avgGlucoseFasting30d: Int = 0,
    val avgGlucosePost7d: Int = 0,
    val avgGlucosePost30d: Int = 0,
    val avgSpO27d: Int = 0,
    val avgSpO230d: Int = 0,
    val avgTemp7d: Float = 0f,
    val avgTemp30d: Float = 0f,
    val avgBp7d: String = "0/0",
    val avgBp30d: String = "0/0",

    // 심박수 정상 범위 이탈 횟수
    val bpmOutOfRangeCount7d: Int = 0,
    val bpmOutOfRangeCount30d: Int = 0,

    // 혈압 정상 범위 이탈 횟수
    val bpOutOfRangeCount7d: Int = 0,
    val bpOutOfRangeCount30d: Int = 0,

    // 공복 혈당 정상 범위 이탈 횟수
    val glucoseFastingOutOfRangeCount7d: Int = 0,
    val glucoseFastingOutOfRangeCount30d: Int = 0,

    // 식후 혈당 정상 범위 이탈 횟수
    val glucosePostOutOfRangeCount7d: Int = 0,
    val glucosePostOutOfRangeCount30d: Int = 0,

    // 체중 정상 범위 이탈 횟수
    val weightOutOfRangeCount7d: Int = 0,
    val weightOutOfRangeCount30d: Int = 0,

    // SpO2 정상 범위 이탈 횟수
    val spo2OutOfRangeCount7d: Int = 0,
    val spo2OutOfRangeCount30d: Int = 0,

    // 체온 정상 범위 이탈 횟수
    val tempOutOfRangeCount7d: Int = 0,
    val tempOutOfRangeCount30d: Int = 0,

    // 낙상 감지 횟수
    val fallCount7d: Int = 0,
    val fallCount30d: Int = 0
)

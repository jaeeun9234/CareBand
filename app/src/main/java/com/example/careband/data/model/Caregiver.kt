package com.example.careband.data.model

data class Caregiver(
    val id: String = "",  // 보호자 UID
    val name: String = "",
    val caredUserId: String = ""  // 케어할 사용자 UID (입력받은 값)
)

package com.example.careband.data.model

data class User(
    val id: String = "",                      // Firebase UID
    val name: String = "",
    val type: UserType = UserType.USER,
    val birth: String = "",
    val gender: String = "",
    val protectedUserId: String? = null       // 보호자 계정일 경우 연결된 사용자 UID
)

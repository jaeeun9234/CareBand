package com.example.careband.data.model

enum class UserType(val label: String) {
    USER("사용자"),
    CAREGIVER("보호자");

    companion object {
        fun fromString(value: String?): UserType? =
            entries.find { it.name == value || it.label == value }
    }
}



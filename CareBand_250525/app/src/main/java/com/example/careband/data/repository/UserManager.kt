// UserManager.kt
package com.example.careband.data.repository

import com.example.careband.data.model.User

object UserManager {
    private val users = mutableListOf<User>()

    fun register(user: User): Boolean {
        if (users.any { it.id == user.id }) return false // 중복 ID 방지
        users.add(user)
        return true
    }

    fun login(id: String, password: String): Boolean {
        return users.any { it.id == id && it.password == password }
    }
}

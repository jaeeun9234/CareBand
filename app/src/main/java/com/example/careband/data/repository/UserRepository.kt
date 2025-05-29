package com.example.careband.data.repository

import com.example.careband.data.model.User
import com.example.careband.data.model.UserType
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun getUser(uid: String): User? {
        return try {
            val document = db.collection("users").document(uid).get().await()
            if (document.exists()) {
                User(
                    id = uid,
                    name = document.getString("name") ?: "",
                    type = UserType.fromString(document.getString("userType")) ?: UserType.USER,
                    birth = document.getString("birth") ?: "",
                    gender = document.getString("gender") ?: "",
                    protectedUserId = document.getString("protectedUserId")
                )
            } else null
        } catch (e: Exception) {
            println("❌ 사용자 정보 가져오기 실패: ${e.message}")
            null
        }
    }

    suspend fun saveUser(
        uid: String,
        user: User
    ): Result<Unit> {
        val userMap = hashMapOf(
            "name" to user.name,
            "userType" to user.type.name,
            "birth" to user.birth,
            "gender" to user.gender,
            "protectedUserId" to user.protectedUserId
        )
        return try {
            db.collection("users").document(uid).set(userMap).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

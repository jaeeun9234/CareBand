package com.example.careband.data.repository

import com.example.careband.data.model.VitalSignsRecord
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.tasks.await

class VitalSignsRepository {

    private val db = FirebaseFirestore.getInstance()

    // 특정 사용자(userId)의 날짜 범위(start ~ end) 내 생체 데이터 가져오기
    suspend fun getVitalSignsInRange(
        userId: String,
        startDate: String,
        endDate: String
    ): List<VitalSignsRecord> {
        return try {
            val snapshot = db.collection("vital_signs")
                .whereEqualTo("userId", userId)
                .whereGreaterThanOrEqualTo("timestamp", startDate)
                .whereLessThanOrEqualTo("timestamp", endDate)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                doc.toObject(VitalSignsRecord::class.java)?.copy(
                    id = doc.id,
                    userId = userId
                )
            }
        } catch (e: Exception) {
            println("❌ VitalSigns 불러오기 실패: ${e.message}")
            emptyList()
        }
    }
}

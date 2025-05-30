package com.example.careband.data.repository

import com.example.careband.data.model.DiseaseRecord
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class DiseaseRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun addDiseaseRecord(userId: String, record: DiseaseRecord) {
        val docRef = db.collection("users").document(userId)
            .collection("disease_records").document()

        val newRecord = record.copy(id = docRef.id, userId = userId)

        try {
            docRef.set(newRecord).await()
            println("✅ 질병 기록 저장 완료: ${newRecord.diseaseName}")
        } catch (e: Exception) {
            println("❌ Firestore 저장 실패: ${e.message}")
        }
    }

    suspend fun getDiseaseRecords(userId: String): List<DiseaseRecord> {
        return try {
            val snapshot = db.collection("users").document(userId)
                .collection("disease_records").get().await()

            println("📥 불러온 질병 기록 개수: ${snapshot.size()}")
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(DiseaseRecord::class.java)?.copy(
                    id = doc.id,
                    userId = userId
                )
            }
        } catch (e: Exception) {
            println("❌ Firestore 불러오기 실패: ${e.message}")
            emptyList()
        }
    }

    suspend fun updateDiseaseRecord(userId: String, record: DiseaseRecord) {
        val docRef = db.collection("users").document(userId)
            .collection("disease_records").document(record.id)

        docRef.set(record.copy(userId = userId)).await()
    }
}

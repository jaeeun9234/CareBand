package com.example.careband.data.repository

import com.example.careband.data.model.DiseaseRecord
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class DiseaseRepository {

    private val db = FirebaseFirestore.getInstance()

    // 질병 기록 추가
    suspend fun addDiseaseRecord(userId: String, record: DiseaseRecord) {
        val diseaseRecordId = "diseaseRecord:$userId:${record.diseaseName}:${record.diagnosedDate}"

        val newRecord = record.copy(id = diseaseRecordId, userId = userId)

        try {
            db.collection("diseaseRecords").document(diseaseRecordId)
                .set(newRecord).await()

            println("✅ 질병 기록 저장 완료: ${newRecord.diseaseName}")
        } catch (e: Exception) {
            println("❌ Firestore 저장 실패: ${e.message}")
        }
    }

    // 사용자 질병 기록 가져오기
    suspend fun getDiseaseRecords(userId: String): List<DiseaseRecord> {
        return try {
            val snapshot = db.collection("diseaseRecords")
                .whereEqualTo("userId", userId) // 🔍 userId 기준으로 필터링
                .get().await()

            println("📥 불러온 질병 기록 개수: ${snapshot.size()}")
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(DiseaseRecord::class.java)?.copy(
                    id = doc.id
                )
            }
        } catch (e: Exception) {
            println("❌ Firestore 불러오기 실패: ${e.message}")
            emptyList()
        }
    }

    // 질병 기록 수정
    suspend fun updateDiseaseRecord(userId: String, record: DiseaseRecord) {
        db.collection("diseaseRecords").document(record.id)
            .set(record.copy(userId = userId)).await()
    }

    // 질병 기록 삭제
    suspend fun deleteDiseaseRecord(userId: String, recordId: String) {
        try {
            db.collection("diseaseRecords").document(recordId).delete().await()
            println("🗑 삭제 완료: $recordId")
        } catch (e: Exception) {
            println("❌ 삭제 실패: ${e.message}")
        }
    }
}

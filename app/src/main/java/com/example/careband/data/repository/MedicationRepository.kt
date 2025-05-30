package com.example.careband.data.repository

import com.example.careband.data.model.MedicationRecord
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MedicationRepository {

    private val db = FirebaseFirestore.getInstance()

    // 복약 기록 추가
    suspend fun addMedicationRecord(userId: String, record: MedicationRecord) {
        val medicationRecordId = "medicationRecord:$userId:${record.medicineName}:${record.startDate}"
        val newRecord = record.copy(id = medicationRecordId, userId = userId)

        try {
            db.collection("medicationRecords").document(medicationRecordId)
                .set(newRecord).await()
            println("✅ 복약 기록 저장 완료: ${newRecord.medicineName}")
        } catch (e: Exception) {
            println("❌ Firestore 저장 실패: ${e.message}")
        }
    }

    // 사용자 복약 기록 가져오기
    suspend fun getMedicationRecords(userId: String): List<MedicationRecord> {
        return try {
            val snapshot = db.collection("medicationRecords")
                .whereEqualTo("userId", userId)
                .get().await()

            println("📥 불러온 복약 기록 개수: ${snapshot.size()}")
            snapshot.documents.mapNotNull { doc ->
                doc.toObject(MedicationRecord::class.java)?.copy(id = doc.id)
            }
        } catch (e: Exception) {
            println("❌ Firestore 불러오기 실패: ${e.message}")
            emptyList()
        }
    }

    // 복약 기록 수정
    suspend fun updateMedicationRecord(userId: String, record: MedicationRecord) {
        db.collection("medicationRecords").document(record.id)
            .set(record.copy(userId = userId)).await()
    }

    // 복약 기록 삭제
    suspend fun deleteMedicationRecord(userId: String, recordId: String) {
        try {
            db.collection("medicationRecords").document(recordId).delete().await()
            println("🗑 삭제 완료: $recordId")
        } catch (e: Exception) {
            println("❌ 삭제 실패: ${e.message}")
        }
    }
}

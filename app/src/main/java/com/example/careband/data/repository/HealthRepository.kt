package com.example.careband.data.repository

import android.util.Log
import com.example.careband.data.model.HealthRecord
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.tasks.await

class HealthRepository {

    private val db = FirebaseFirestore.getInstance()

    fun saveHealthRecord(
        userId: String,
        record: HealthRecord,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        val finalRecord = record.copy(
            id = "healthRecord:$userId:${record.date}",
            userId = userId
        )

        FirebaseFirestore.getInstance()
            .collection("healthRecords")
            .document(userId)
            .collection("records")
            .document(record.date)
            .set(finalRecord)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "저장 실패") }
    }

    fun getHealthRecord(
        userId: String,
        date: String,
        onComplete: (HealthRecord?) -> Unit
    ) {
        db.collection("healthRecords")
            .document(userId)
            .collection("records")
            .document(date)
            .get()
            .addOnSuccessListener { snapshot ->
                val record = snapshot.toObject(HealthRecord::class.java)
                onComplete(record)
            }
            .addOnFailureListener {
                Log.e("Firestore", "데이터 불러오기 실패: ${it.message}")
                onComplete(null)
            }
    }
    suspend fun getHealthRecordsInRange(userId: String, startDate: String, endDate: String): List<HealthRecord> {
        return Firebase.firestore.collection("healthRecords")
            .document(userId)
            .collection("records")
            .whereGreaterThanOrEqualTo("date", startDate)
            .whereLessThanOrEqualTo("date", endDate)
            .get()
            .await()
            .documents.mapNotNull { it.toObject(HealthRecord::class.java) }
    }
}

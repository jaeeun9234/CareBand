package com.example.careband.data.repository

import com.example.careband.data.model.HealthRecord
import com.google.firebase.firestore.FirebaseFirestore

class HealthRepository {

    private val db = FirebaseFirestore.getInstance()

    fun saveHealthRecord(
        userId: String,
        record: HealthRecord,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        db.collection("users")
            .document(userId)
            .collection("health_records")
            .document(record.date)
            .set(record)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onFailure(e.message ?: "Unknown error") }
    }

    fun getHealthRecord(
        userId: String,
        date: String,
        onResult: (HealthRecord?) -> Unit
    ) {
        db.collection("users")
            .document(userId)
            .collection("health_records")
            .document(date)
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val record = doc.toObject(HealthRecord::class.java)
                    onResult(record)
                } else {
                    onResult(null)
                }
            }
            .addOnFailureListener { onResult(null) }
    }
}

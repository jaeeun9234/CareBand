package com.example.careband.data.repository

import com.example.careband.data.model.MedicationRecord
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class MedicationRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun addMedicationRecord(userId: String, record: MedicationRecord) {
        val docRef = db.collection("users").document(userId)
            .collection("medication_records").document()

        val newRecord = record.copy(id = docRef.id)
        docRef.set(newRecord).await()
    }

    suspend fun getMedicationRecords(userId: String): List<MedicationRecord> {
        val snapshot = db.collection("users").document(userId)
            .collection("medication_records").get().await()
        return snapshot.documents.mapNotNull { it.toObject<MedicationRecord>() }
    }

    suspend fun updateMedicationRecord(userId: String, record: MedicationRecord) {
        val docRef = db.collection("users").document(userId)
            .collection("medication_records").document(record.id)
        docRef.set(record).await()
    }
}

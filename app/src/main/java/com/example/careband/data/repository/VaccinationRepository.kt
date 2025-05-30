package com.example.careband.data.repository

import com.example.careband.data.model.VaccinationRecord
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class VaccinationRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun addVaccinationRecord(userId: String, record: VaccinationRecord) {
        val docRef = db.collection("users").document(userId)
            .collection("vaccination_records").document()

        val newRecord = record.copy(id = docRef.id)
        docRef.set(newRecord).await()
    }

    suspend fun getVaccinationRecords(userId: String): List<VaccinationRecord> {
        val snapshot = db.collection("users").document(userId)
            .collection("vaccination_records").get().await()
        return snapshot.documents.mapNotNull { it.toObject<VaccinationRecord>() }
    }

    suspend fun updateVaccinationRecord(userId: String, record: VaccinationRecord) {
        val docRef = db.collection("users").document(userId)
            .collection("vaccination_records").document(record.id)
        docRef.set(record).await()
    }
}

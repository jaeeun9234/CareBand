package com.example.careband.data.repository

import com.example.careband.data.model.VaccinationRecord
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class VaccinationRepository {

    private val db = FirebaseFirestore.getInstance()

    suspend fun addVaccinationRecord(userId: String, record: VaccinationRecord) {
        val id = record.id.ifBlank { "vaccination:$userId:${record.vaccineName}:${record.date}" }
        val docRef = db.collection("vaccinationRecords").document(id)

        val newRecord = record.copy(id = id, userId = userId)
        docRef.set(newRecord).await()
    }

    suspend fun getVaccinationRecords(userId: String): List<VaccinationRecord> {
        return try {
            val snapshot = db.collection("vaccinationRecords")
                .whereEqualTo("userId", userId)
                .get().await()

            snapshot.documents.mapNotNull { it.toObject(VaccinationRecord::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun updateVaccinationRecord(userId: String, record: VaccinationRecord) {
        val docRef = db.collection("vaccinationRecords").document(record.id)
        docRef.set(record.copy(userId = userId)).await()
    }

    suspend fun deleteVaccinationRecord(userId: String, recordId: String) {
        db.collection("vaccinationRecords").document(recordId).delete().await()
    }
}

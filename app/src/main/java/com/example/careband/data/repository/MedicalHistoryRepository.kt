package com.example.careband.data.repository

import com.example.careband.data.model.*
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.tasks.await

class MedicalHistoryRepository {

    private val db = FirebaseFirestore.getInstance()

    // ---------------- 접종 기록 ----------------
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

    // ---------------- 복약 기록 ----------------
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

    // ---------------- 질병 기록 ----------------
    suspend fun addDiseaseRecord(userId: String, record: DiseaseRecord) {
        val docRef = db.collection("users").document(userId)
            .collection("disease_records").document()

        val newRecord = record.copy(id = docRef.id)
        docRef.set(newRecord).await()
    }

    suspend fun getDiseaseRecords(userId: String): List<DiseaseRecord> {
        val snapshot = db.collection("users").document(userId)
            .collection("disease_records").get().await()
        return snapshot.documents.mapNotNull { it.toObject<DiseaseRecord>() }
    }

    suspend fun updateDiseaseRecord(userId: String, record: DiseaseRecord) {
        val docRef = db.collection("users").document(userId)
            .collection("disease_records").document(record.id)
        docRef.set(record).await()
    }
}

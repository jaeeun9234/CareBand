package com.example.careband.data.repository

import android.util.Log
import com.example.careband.data.model.Caregiver
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class CaregiverRepository {

    private val db = FirebaseFirestore.getInstance()
    private val caregiversCollection = db.collection("caregivers")

    /** 보호자 정보 저장 **/
    fun saveCaregiver(
        caregiver: Caregiver,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        caregiversCollection
            .document(caregiver.id)
            .set(caregiver)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                Log.e("Firestore", "Caregiver 저장 실패: ${e.message}")
                onFailure(e.message ?: "저장 실패")
            }
    }

    /** 보호자 ID로 해당 케어 사용자 ID 가져오기 **/
    suspend fun getCaredUserId(caregiverId: String): String? {
        return try {
            val snapshot = caregiversCollection.document(caregiverId).get().await()
            snapshot.getString("caredUserId")
        } catch (e: Exception) {
            Log.e("Firestore", "Caregiver 정보 가져오기 실패: ${e.message}")
            null
        }
    }

    /** 보호자 전체 정보 가져오기 (필요 시) **/
    suspend fun getCaregiver(caregiverId: String): Caregiver? {
        return try {
            val snapshot = caregiversCollection.document(caregiverId).get().await()
            snapshot.toObject(Caregiver::class.java)
        } catch (e: Exception) {
            Log.e("Firestore", "Caregiver 불러오기 실패: ${e.message}")
            null
        }
    }
}

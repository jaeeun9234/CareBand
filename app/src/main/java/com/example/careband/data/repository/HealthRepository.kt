package com.example.careband.data.repository

import android.util.Log
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
        db.collection("healthRecords")        // 1단계: 컬렉션 이름
            .document(userId)                // 2단계: 사용자별로 문서
            .collection("records")           // 3단계: 날짜별 기록 서브 컬렉션
            .document(record.date)           // 4단계: 날짜를 문서 ID로
            .set(record)                     // 5단계: 전체 HealthRecord 저장
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { e ->
                onFailure(e.message ?: "알 수 없는 오류")
            }
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
}

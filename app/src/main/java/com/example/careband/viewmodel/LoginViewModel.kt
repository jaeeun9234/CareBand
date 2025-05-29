package com.example.careband.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    /**
     * Firebase 인증 기반 로그인
     */
    fun login(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onFailure(it.message ?: "로그인 실패")
            }
    }

    /**
     * Firebase 인증 기반 회원가입
     * → 이후 Firestore 연동은 AuthViewModel 또는 별도 로직에서 처리
     */
    fun register(
        email: String,
        password: String,
        onSuccess: (String) -> Unit, // UID 반환
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val uid = it.user?.uid
                if (uid != null) {
                    onSuccess(uid)
                } else {
                    onFailure("회원가입은 성공했지만 UID를 가져오지 못했습니다.")
                }
            }
            .addOnFailureListener {
                onFailure(it.message ?: "회원가입 실패")
            }
    }
}

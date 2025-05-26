package com.example.careband.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    fun currentUserUid(): String? {
        return auth.currentUser?.uid
    }

    fun login(
        email: String,
        password: String,
        onSuccess: (String) -> Unit, // UID 전달
        onFailure: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = auth.currentUser?.uid
                    if (uid != null) {
                        onSuccess(uid)
                    } else {
                        onFailure("로그인 성공했지만 UID를 가져오지 못했습니다.")
                    }
                } else {
                    onFailure(task.exception?.message ?: "로그인 실패")
                }
            }
    }

    fun register(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(task.exception?.message ?: "회원가입 실패")
                }
            }
    }
}

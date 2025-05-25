package com.example.careband.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.careband.data.model.UserType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    private val _userType = MutableStateFlow<UserType?>(null)
    val userType: StateFlow<UserType?> = _userType

    private val _userName = MutableStateFlow<String?>(null)
    val userName: StateFlow<String?> = _userName

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    init {
        checkLoginStatus()
    }

    fun checkLoginStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            _isLoggedIn.value = true
            loadUserData(currentUser.uid)
        } else {
            _isLoggedIn.value = false
            _userType.value = null
            _userName.value = null
        }
    }

//    fun loadUserData(uid: String) {
//        viewModelScope.launch {
//            db.collection("users").document(uid).get()
//                .addOnSuccessListener { document ->
//                    if (document != null && document.exists()) {
//                        _userName.value = document.getString("name")
//                        _userType.value = when (document.getString("userType")) {
//                            "USER" -> UserType.USER
//                            "CAREGIVER" -> UserType.CAREGIVER
//                            else -> null
//                        }
//                    }
//                }
//        }
//    }

    fun loadUserData(uid: String) {
        println("📥 [loadUserData] Loading for uid = $uid")
        db.collection("users").document(uid).get()
            .addOnSuccessListener { document ->
                if (document != null && document.exists()) {
                    val name = document.getString("name")
                    val typeStr = document.getString("userType")
                    val type = when (typeStr) {
                        "USER" -> UserType.USER
                        "CAREGIVER" -> UserType.CAREGIVER
                        else -> null
                    }
                    println("✅ [loadUserData] name = $name, type = $typeStr → parsed: $type")
                    _userName.value = name
                    _userType.value = type
                } else {
                    println("🚫 [loadUserData] 문서가 없거나 null")
                }
            }
            .addOnFailureListener {
                println("❌ [loadUserData] 오류: ${it.message}")
            }
    }

    fun registerUser(email: String, password: String, name: String, userType: UserType, onSuccess: () -> Unit, onError: (String) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener

                val user = hashMapOf(
                    "name" to name,
                    "userType" to userType.name  // "USER" or "CAREGIVER"
                )

                db.collection("users").document(uid).set(user)
                    .addOnSuccessListener {
                        onSuccess()
                    }
                    .addOnFailureListener {
                        onError("Firestore 저장 실패: ${it.message}")
                    }
            }
            .addOnFailureListener {
                onError("회원가입 실패: ${it.message}")
            }
    }



    fun logout() {
        auth.signOut()
        _isLoggedIn.value = false
        _userType.value = null
        _userName.value = null
    }
}
